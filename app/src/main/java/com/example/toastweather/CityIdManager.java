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

    private InputStream inputStream;
    private SharedPreferences sharedPreferences;
    private static CityIdManager cityIdManager;

    /**
     * 单例模式获得城市ID管理器
     * @param inputStream 输入流来读取raw文本
     * @param sharedPreferences 用来制作2500组城市ID键值对
     */
    private CityIdManager(InputStream inputStream, SharedPreferences sharedPreferences) {
        this.inputStream = inputStream;
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * init manager out side
     * @param inputStream for constructor
     * @param sharedPreferences for constructor
     * @return
     */
    public static CityIdManager setCityIdManager(InputStream inputStream, SharedPreferences sharedPreferences){
        CityIdManager.cityIdManager = new CityIdManager(inputStream,sharedPreferences);
        return cityIdManager;
    }

    /**
     * get manager outside
     * @return
     */
    public static CityIdManager getManager() {
        return cityIdManager;
    }
    
    /**
     * 把天气ID数据保存到SharedPreferences中去（Only for the first time）
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
     * Get city's ID in database.
     * @param city city's name in string
     * @return String the ID
     */
    public String getID(String city){
        return sharedPreferences.getString(city,"101200101");
    }

    /**
     *  whether the city's name is valid or not
     * @param city city's name in string
     * @return true if it's valid which is in database, false if it's not valid
     */
    public boolean isValid(String city){
        if (sharedPreferences.getString(city,"1").equals("1")){
            return false;
        }else {
            return true;
        }

    }

}
