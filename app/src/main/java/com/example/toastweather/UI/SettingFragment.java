package com.example.toastweather.UI;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.toastweather.Support.CityIdManager;
import com.example.toastweather.R;
import com.example.toastweather.Service.UpdateBinder;
import com.example.toastweather.Service.UpdateDataService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Xgl on 2017/7/19.
 */

public class SettingFragment extends Fragment {

    private Spinner spinner;
    private DetailFragment detailFragment;
    private ArrayAdapter<String> adapter;
    private UpdateBinder updateBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            updateBinder = (UpdateBinder) service;
            updateBinder.setSharedPreference(getActivity());//传入活动的实例，方便进行数据的写入
            updateBinder.startUpdate();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("onServiceDisconnected","ServiceDisconnected");
            UpdateBinder.setKeepRunning(false);
        }


    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //TODO: 添加Service进行Toast推送
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //初始化spinner
        initSpinner(view);

        //添加按钮的监听器
        initButtonFunc(view);

        //获得另一个碎片的实例，从而调用其方法
        detailFragment = (DetailFragment) getFragmentManager().findFragmentByTag("DETAIL");

        //init switch
        initSwitch(view);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ToastWeather");
        builder.setMessage("开源在:\ngithub.com/GrayXu/ToastWeather \n\n图标:www.iconfont.cn\n\n天气数据:www.weather.com.cn\n\nEmail:chh1569348@gmail.com\n希望能收到反馈 or issue"+new String(Character.toChars(0x1F64F)));
        builder.setCancelable(false);

        builder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ((LinearLayout) view.findViewById(R.id.layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Activity activity = getActivity();

            if (activity != null) {//防止第一次加载的时候无活动造成空指针（因为主界面不是它，默认一开始没加载起来，但是onHiddenChanged会跑起来）
                SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                String newCity = sharedPreferences.getString("newCity", "武汉");
                String oldCity = sharedPreferences.getString("oldCity", "武汉");

                if (!newCity.equals(oldCity)) {//如果前后文字不同，重新设置detail界面并刷新
                    Log.d("onHiddenChanged城市不同", "取出newCity键值对，为" + newCity + ",oldCity为" + oldCity);
                    detailFragment.updateWeatherData(newCity);
                    detailFragment.updateWebView(newCity);
                    //更新oldCity
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("oldCity", newCity);
                    editor.apply();
                    Log.d("onHiddenChanged", "更新oldCity成功");
                }
            }

        }
    }

    /**
     * 初始化spinner
     * @param view
     */
    private void initSpinner(View view) {
        spinner = (Spinner) view.findViewById(R.id.spinnerCity);
        List<String> stringList = new ArrayList<>();
        stringList.add("武汉");//若没有历史记录，则使用武汉
        Set<String> stringSet = new HashSet<>(stringList);
        Set<String> strings = getActivity().getPreferences(Context.MODE_PRIVATE).getStringSet("spinnerCities", stringSet);
        stringList = new ArrayList<>(strings);//城市列表
        adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner, stringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getActivity().getPreferences(Context.MODE_PRIVATE).getInt("cityPosition", 0));

        //长按删除Item功能
        spinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("initSpinner","监听到长按事件");
                final String citySelected = spinner.getSelectedItem().toString();
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("确定删除"+citySelected+"吗？");
                dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spinner.setSelection(0);
                        adapter.remove(citySelected);
                        Toast.makeText(getActivity(),"已删除",Toast.LENGTH_SHORT).show();
                        Log.i("initSpinner","已删除城市");
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    /**
     * 给外部获得Adapter来保存城市列表
     */
    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    /**
     * 初始化添加按钮的功能
     *
     * @param view
     */
    private void initButtonFunc(final View view) {
        ImageButton button = (ImageButton) view.findViewById(R.id.buttAddCity);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View viewInside = factory.inflate(R.layout.dialog_edit_text, null);
                final EditText editText = (EditText) viewInside.findViewById(R.id.dialogEditText);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("增加新城市");
                dialog.setView(viewInside);

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText == null) {
                        } else {
                            String newCity = editText.getText().toString();

                            if (!CityIdManager.getManager().isValid(newCity)) {
                                Toast.makeText(getActivity(), "输入的城市名不合法，试试去掉“县” “市”...", Toast.LENGTH_SHORT).show();
                            } else {
                                boolean notRepeat = true;//
                                for (int i = 0; i < adapter.getCount(); i++) {
                                    if (newCity.equals(adapter.getItem(i))) {
                                        Toast.makeText(getActivity(), "输入的城市已存在", Toast.LENGTH_SHORT).show();
                                        notRepeat = false;
                                    }
                                }
                                if (notRepeat) {
                                    adapter.add(newCity);
                                    Log.i("initButtonFunc", "成功添加城市：" + newCity);
                                    Toast.makeText(getActivity(), "成功添加!", Toast.LENGTH_SHORT).show();
                                    spinner.setSelection(adapter.getPosition(newCity));
                                }

                            }
                        }

                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * Initialize switch. And bind the updating service.
     * @param view
     */
    private void initSwitch(View view){
        Switch switchToast = (Switch) view.findViewById(R.id.switchToast);

        boolean old = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("isChecked",false);
        switchToast.setChecked(old);
        if (old){
            Intent intent = new Intent(getActivity(), UpdateDataService.class);
            UpdateBinder.setKeepRunning(true);
            getActivity().startService(intent);
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        switchToast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){//true selected
                    UpdateBinder.setKeepRunning(true);
                    Intent intent = new Intent(getActivity(), UpdateDataService.class);
                    getActivity().startService(intent);
                    getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
                }else {//false
                    UpdateBinder.setKeepRunning(false);
                    getActivity().unbindService(connection);
                    getActivity().stopService(new Intent(getActivity(), UpdateDataService.class));
                }
            }
        });
    }

    @Override
    public void onDestroy() {

        try{
            getActivity().unbindService(connection);
        }catch (Exception e){
        }

        super.onDestroy();
    }
}
