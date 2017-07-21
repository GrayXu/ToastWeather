package com.example.toastweather;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xgl on 2017/7/19.
 */

public class DetailFragment extends Fragment {

    private List<Weather> weatherList = new ArrayList<>();
    private WeatherRequest weatherRequest;
    private View view = null;
    private WeatherAdapter weatherAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details, container, false);

        //载入离线缓存
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        ((TextView) view.findViewById(R.id.textTemper)).setText(sharedPreferences.getString("temper","0℃"));
        ((TextView) view.findViewById(R.id.textAQI)).setText(sharedPreferences.getString("AQI",":0"));
        ((TextView) view.findViewById(R.id.textToday)).setText(sharedPreferences.getString("today","载入中..."));

        //装填数据（默认为武汉）
        String oldCity = getActivity().getPreferences(Context.MODE_PRIVATE).getString("oldCity","武汉");
        initWeatherData(oldCity);
        //初始化WebView
        initWebView(oldCity);

        Log.d("initWeatherData","RecyclerView已经重新布局");
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());//布局管理器
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.addItemDecoration(new MyItemDecoration());//TODO:设置分割线
        weatherAdapter = new WeatherAdapter(weatherList);
        recyclerView.setAdapter(weatherAdapter);//适配器装载
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            Log.d("生命周期","Detail is hidden");
        }else {
            Log.d("生命周期","Detail is in show");

            //保存选择的城市
            Activity activity = getActivity();
            if (activity != null){//防止第一次加载的时候无活动造成空指针
                Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerCity);
                String newCity = spinner.getSelectedItem().toString();
                SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                editor.putString("newCity",newCity);
                editor.apply();
                Log.i("onHiddenChanged","spinner中选择的城市是"+newCity+",并保存在newCity键值对里了");
            }
        }
    }

    /**
     * 初始化天气预报数据并且更新顶端的当前天气View和RecyclerView
     */
     void initWeatherData(String city) {//Friendly
        final String cityFinal = new String(city);
        //初始化天气预报信息（添加到成员变量List中去）
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                weatherRequest = new WeatherRequest(cityFinal);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if (weatherRequest.getRequestResult() == 1) {//TODO:没有网络的处理
                    Log.d("onPostExecute", "已经成功使用"+cityFinal+"初始化weatherRequest了");
                    // TODO: 利用缓存实现快速读取和离线读取
                    //天气预报的信息读取并更新weatherList
                    weatherList.clear();
                    for (int i = 0; i <= 4; i++) {
                        //获取图片ID
                        int id;
                        String weatherType = weatherRequest.getSomeDaydata(i).get("天气");
                        if (weatherType.equals("阴")) {
                            id = R.drawable.yin;
                        } else if (weatherType.equals("多云")) {
                            id = R.drawable.cloudy;
                        } else if (weatherType.equals("晴")) {
                            id = R.drawable.sunny;
                        } else {
                            id = R.drawable.yin;
                        }
                        //获取天气简介
                        String info = weatherRequest.getSomeDayInfo(i);

                        weatherList.add(new Weather(id, info));
                    }
                    Activity activity = getActivity();
                    //装填进当前详情的TextView
//                    sb.append(weatherRequest.getCity() + "当前的温度:" + weatherRequest.getTemperatureNow() + "℃      AQI:" + weatherRequest.getAQI() + "\n" + weatherRequest.getColdTips());
                    ((TextView) activity.findViewById(R.id.textToday)).setText(weatherRequest.getColdTips());
                    ((TextView) activity.findViewById(R.id.textAQI)).setText(":"+weatherRequest.getAQI());
                    ((TextView) activity.findViewById(R.id.textTemper)).setText(weatherRequest.getTemperatureNow() + "℃");

                    //刷新位置
                    weatherAdapter.notifyItemRangeChanged(0,4);
                }else {
                    Toast.makeText(getActivity(),"网络出现问题，请检查网络设置:)",Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(aVoid);
            }
        }.execute();


    }

    /**
     * 初始化webview
     * @param city 城市名 //TODO:搭SQLite配对城市和编号
     */
    void initWebView(String city){//Friendly
        //WebView的数据装填
        String cityID = ((MainActivity) getActivity()).getCityIdManager().getID(city);
        //TODO: 去除广告的div等网页预处理
        WebView webView = (WebView) view.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setDatabasePath("");
        webSettings.setAppCachePath("");
        webSettings.setAppCacheEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl("http://m.weather.com.cn/mhours/"+cityID+".shtml");
    }
}
