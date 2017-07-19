package com.example.toastweather;

import android.app.Fragment;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xgl on 2017/7/19.
 */

public class DetailFragment extends Fragment {

    private List<Weather> weatherList = new ArrayList<>();
    WeatherRequest weatherRequest;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        //装填数据
        initWeatherData();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());//布局管理器
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new MyItemDecoration());//设置分割线
        recyclerView.setAdapter(new WeatherAdapter(weatherList));//适配器装载

        //WebView的数据装填
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

        webView.loadUrl("http://m.weather.com.cn/mhours/101230501.shtml");

        return view;
    }


    /**
     * 初始化天气预报数据并且更新顶端的当前天气View和RecyclerView
     */
    private void initWeatherData() {

        //初始化天气预报信息（添加到成员变量List中去）
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                weatherRequest = new WeatherRequest("泉州");
                Log.d("doInBackground", "已经成功初始化weatherRequest了");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (weatherRequest.getRequestResult() == 1) {

                    // TODO: 利用缓存实现快速读取和离线读取
                    //天气预报的信息读取并进行装填
                    for (int i = 0; i <= 4; i++) {
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
                        String info = weatherRequest.getSomeDayInfo(i);

                        weatherList.add(new Weather(id, info));

                        StringBuilder sb = new StringBuilder();
                        sb.append(weatherRequest.getCity() + "当前的温度:" + weatherRequest.getTemperatureNow() + "      AQI:" + weatherRequest.getAQI() + "\n感冒提醒: " + weatherRequest.getColdTips());
                        TextView textView = (TextView) getActivity().findViewById(R.id.textToday);
                        textView.setText(sb.toString());
                    }
                }
                super.onPostExecute(aVoid);
            }
        }.execute();

    }
}
