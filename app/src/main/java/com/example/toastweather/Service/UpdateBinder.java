package com.example.toastweather.Service;

/**
 * Created by Xgl on 2017/7/31.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.util.Log;

import com.example.toastweather.R;
import com.example.toastweather.Support.Weather;
import com.example.toastweather.Support.WeatherRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xgl on 2017/7/31.
 */

public class UpdateBinder extends Binder {

    private static boolean keepRunning = true;
    private WeatherRequest weatherRequest;
    private List<Weather> weatherList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    public static void setKeepRunning(boolean flag){
        keepRunning = flag;
    }

    public void startUpdate() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (keepRunning){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String city = sharedPreferences.getString("newCity","武汉");
                    try {
                        Thread.sleep(1200000);//one update for 20 min (20*1000*60=
                        updateData(city);
                        Log.i("startUpdate","更新了"+city+"的信息");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("startUpdate", "出现打断错误");
                    }
                }
                Log.i("startUpdate","更新结束，线程运行完毕");
            }
        }).start();

    }

    /**
     * 让碎片把活动的实例传进来，从而操作文件
     * @param activity
     */
    public void setSharedPreference(Activity activity) {
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    /**
     * 更新SharedPreferences
     * @param city
     */
    public void updateData(String city){

        final String cityFinal = new String(city);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                weatherRequest = new WeatherRequest(cityFinal);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if (weatherRequest.getRequestResult() == 1) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("AQI",":"+weatherRequest.getAQI());
                    editor.putString("temper",weatherRequest.getTemperatureNow()+"℃");
                    editor.putString("today",weatherRequest.getColdTips());

                    for (int i = 0; i <= 4; i++) {
                        int id;
                        String weatherType = weatherRequest.getSomeDaydata(i).get("天气");
                        if (weatherType.equals("阴")) {
                            id = R.drawable.yin;
                        } else if (weatherType.equals("多云")) {
                            id = R.drawable.cloudy;
                        } else if (weatherType.equals("晴")) {
                            id = R.drawable.sunny;
                        } else if (weatherType.equals("小雨")){
                            id = R.drawable.rainy;
                        } else if (weatherType.equals("阵雨") || weatherType.equals("大雨")){
                            id = R.drawable.rainy_l;
                        } else if (weatherType.indexOf("雷")!=-1 || weatherType.equals("暴雨")){
                            id = R.drawable.thunder;
                        } else if (weatherType.indexOf("雪")!=-1){
                            id = R.drawable.snowy;
                        } else if (weatherType.indexOf("中雨")!=-1){
                            id = R.drawable.rainy_m;
                        } else {
                            id = R.drawable.error;//default
                        }
                        //获取天气简介
                        String info = weatherRequest.getSomeDayInfo(i);

                        //保存天气预报信息
                        weatherList.add(new Weather(id, info));
                        editor.putInt("day" + i + "id", id);
                        editor.putString("day" + i + "info", info);

                    }
                    editor.apply();

                } else {
                    Log.i("onPostExecute","后台更新失败，没有网络");
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

}

