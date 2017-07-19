package com.example.toastweather;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Xgl on 2017/7/19.
 */

public class SettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //TODO: 设置按钮添加触发
        //TODO: 添加服务进行Toast推送
        View view = inflater.inflate(R.layout.fragment_settings,container,false);

        return view;
    }
}
