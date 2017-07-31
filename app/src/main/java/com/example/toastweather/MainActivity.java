package com.example.toastweather;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private DetailFragment detailFragment;
    private SettingFragment settingFragment;
    private CityIdManager cityIdManager;

    /**
     * 给Fragment获取cityID使用
     * @return
     */
    public CityIdManager getCityIdManager() {
        return cityIdManager;
    }

    /**
     * Navigation Item Selected Listener.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        //根据不同的选择，展示不同的碎片
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            fragmentTransaction = getFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_Details:
                    fragmentTransaction.show(detailFragment).hide(settingFragment);
                    fragmentTransaction.commit();
                    return true;

                case R.id.navigation_Settings:
                    fragmentTransaction.show(settingFragment).hide(detailFragment);
                    fragmentTransaction.commit();
                    return true;

            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化cityIdManager
        InputStream inputStream = getResources().openRawResource(R.raw.cityid);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        cityIdManager = CityIdManager.setCityIdManager(inputStream, sharedPreferences);

        if(sharedPreferences.getBoolean("isFirst",true)){//仅第一次运行进行数据库的初始化
            Log.i("onCreat","it is first");
            new AsyncTask<Void, Void, Void>() {//耗时操作（是否会有操作前后的问题？）
                @Override
                protected Void doInBackground(Void... params) {
                    cityIdManager.initSharedPreferences();
                    return null;
                }
            }.execute();

            sharedPreferences.edit().putBoolean("isFirst",false).apply();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //初始化Fragment管理
        fragmentTransaction = getFragmentManager().beginTransaction();
        detailFragment = new DetailFragment();
        settingFragment = new SettingFragment();
        fragmentTransaction.add(R.id.content, detailFragment, "DETAIL");
        fragmentTransaction.add(R.id.content, settingFragment, "SETTING");

        //初始化第一个碎片作为主界面
        fragmentTransaction.show(detailFragment).hide(settingFragment);
        fragmentTransaction.commit();

    }

    /**
     * 存档存档
     */
    @Override
    protected void onStop() {
        //执行存档操作
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        super.onStop();
        int position = ((Spinner) findViewById(R.id.spinnerCity)).getSelectedItemPosition();
        editor.putInt("cityPosition", position);
        editor.putString("AQI", ((TextView) findViewById(R.id.textAQI)).getText().toString());
        editor.putString("temper", ((TextView) findViewById(R.id.textTemper)).getText().toString());
        editor.putString("today", ((TextView) findViewById(R.id.textToday)).getText().toString());
        editor.putString("newCity",((Spinner) findViewById(R.id.spinnerCity)).getSelectedItem().toString());

        //保存spinner里面的城市列表
        Set<String> citySet = new HashSet<>();
        ArrayAdapter<String> citiesSpinnerAdapter = ((SettingFragment) getFragmentManager().findFragmentByTag("SETTING")).getAdapter();
        for (int i = 0; i < citiesSpinnerAdapter.getCount(); i++) {
            citySet.add(citiesSpinnerAdapter.getItem(i));
        }
        editor.putStringSet("spinnerCities",citySet);

        editor.putBoolean("isChecked", ((Switch) findViewById(R.id.switchToast)).isChecked());
        editor.apply();
        Log.v("onStop","已经保存数据");
    }
}
