package com.example.toastweather;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

/**
 * Created by Xgl on 2017/7/19.
 */

public class SettingFragment extends Fragment {

    DetailFragment detailFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //TODO: 添加Service进行Toast推送
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //根据上次的保存，设置spinner位置
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerCity);
        if (spinner==null){
            Log.e("onCreateView(Setting","spinner is null");
        }else {
            spinner.setSelection(getActivity().getPreferences(Context.MODE_PRIVATE).getInt("cityPosition",0));
        }

        android.app.FragmentManager fragmentManager = getFragmentManager();
        //获得另一个碎片的实例，从而调用其方法
        detailFragment = (DetailFragment) fragmentManager.findFragmentByTag("DETAIL");
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Log.d("生命周期", "Setting is hidden");

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
                    Log.d("onHiddenChanged","更新oldCity成功");
                }
            }

        } else {
            Log.d("生命周期", "Setting is in show");
        }
    }

}
