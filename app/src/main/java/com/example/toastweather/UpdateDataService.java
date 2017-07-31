package com.example.toastweather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdateDataService extends Service {

    private UpdateBinder mBinder = new UpdateBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //服务被创建的时候被调用
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //每次服务启动的时候被调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("onStartCommand", "服务开始");
        return super.onStartCommand(intent, flags, startId);
    }

    //服务被销毁的时候调用
    @Override
    public void onDestroy() {
        Log.i("onDestroy", "服务被销毁");
        UpdateBinder.setKeepRunning(false);//结束服务
        super.onDestroy();
    }


}
