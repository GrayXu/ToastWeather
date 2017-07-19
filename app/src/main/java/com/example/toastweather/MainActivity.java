package com.example.toastweather;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        //根据不同的选择，展示不同的碎片
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_Details:
                    fragmentTransaction.replace(R.id.content, new DetailFragment());
                    fragmentTransaction.commit();

                    Log.d("navigation_details", "navigation_details is clicked");
                    return true;
                case R.id.navigation_Settings:
                    fragmentTransaction.replace(R.id.content, new SettingFragment());
                    fragmentTransaction.commit();

                    Log.d("navigation_settings", "navigation_settings is clicked");
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

        //初始化主界面
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, new DetailFragment());
        fragmentTransaction.commit();

    }

}
