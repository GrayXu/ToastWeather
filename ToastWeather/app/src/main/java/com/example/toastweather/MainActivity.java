package com.example.toastweather;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private DetailFragment detailFragment;
    private SettingFragment settingFragment;

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

    @Override
    protected void onStop() {
        super.onStop();
        int position = ((Spinner) findViewById(R.id.spinnerCity)).getSelectedItemPosition();
        getPreferences(MODE_PRIVATE).edit().putInt("cityPosition", position).apply();

        Log.v("onStop","已经保存cityPosition为"+position+"惹");
    }
}
