package com.example.toastweather;

/**
 * Created by Xgl on 2017/7/21.
 */

import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * 城市ID管理器
 */
public class CityIdManager {

    InputStream inputStream;
    SharedPreferences sharedPreferences;

    public CityIdManager(InputStream inputStream, SharedPreferences sharedPreferences) {
        this.inputStream = inputStream;
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * 把天气ID数据保存到SharedPreferences中去（Only for first time）
     * @return true if it's running successfully, false if it meets exception.
     */
    public boolean initSharedPreferences(){
        Long startTime = System.currentTimeMillis();

        InputStreamReader inputStreamReader;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            inputStreamReader = new InputStreamReader(this.inputStream,"GBK");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line=reader.readLine())!=null){
                String[] strings = line.split("=");
                editor.putString(strings[1],strings[0]);
            }
            editor.apply();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        Long endTime = System.currentTimeMillis();
        Log.i("initSharedPreferences","用时为"+(endTime-startTime));
        return true;
    }

    /**
     * 获得城市对应的ID
     * @param city 城市名字
     * @return String 城市对应的数字ID
     */
    public String getID(String city){
        return sharedPreferences.getString(city,"101200101");
    }

}
