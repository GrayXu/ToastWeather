package com.example.toastweather.Support;

/**
 * Created by Xgl on 2017/8/4.
 */

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.Map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Xgl on 2017/8/2.
 * <p>You can use this file as a lib to get hourly weather forecast in the next 24 hours</p>
 * <p>Powered by HtmlUnit</p>
 */

public class HourlyWeather {

    private List<String> times;
    private List<String> weathers;
    private List<String> temps;
    private String url;
    private static int TOTAL_NUM = 24;
    private boolean isSuccessful;

//    /**
//     * Test
//     */
//    public static void main(String[] args) {
//        HourlyWeather hourlyWeather = new HourlyWeather("101230505");
//        if (hourlyWeather.initData()){
//            //Then you can do everything you want.
//        }
//
//    }

    /**
     * Initialize url and lists in constructor.
     */
    public HourlyWeather(String cityID) {
        times = new ArrayList<>();
        weathers = new ArrayList<>();
        temps = new ArrayList<>();
        url = new String("http://m.weather.com.cn/mhours/"+cityID+".shtml");
        isSuccessful = initData();
    }

    /**
     * Initialize the hourly weather data( in those lists of HourlyWeather)
     * @return Successful if true, failed if false
     */
    public boolean initData(){

        final WebClient webClient = new WebClient();
        final HtmlPage page;

        //set web client options
        WebClientOptions webClientOptions = webClient.getOptions();
        webClientOptions.setCssEnabled(false);
        webClientOptions.setUseInsecureSSL(true);
        webClientOptions.setActiveXNative(false);
        webClientOptions.setThrowExceptionOnScriptError(false);
        webClient.waitForBackgroundJavaScript(50000);
        webClientOptions.setThrowExceptionOnScriptError(false);
        webClientOptions.setThrowExceptionOnFailingStatusCode(false);

        try {
            page = webClient.getPage(url);

            List<?> items = page.getByXPath("//li");//TODO：Perhaps it can be improved to reduce the wasted time because of such a violent algorithms.

            for (int i = 0; i < TOTAL_NUM; i++) {
                HtmlListItem htmlListItem = (HtmlListItem) items.get(i);
                String time = htmlListItem.getFirstChild().toString();
                times.add(time);
            }

            for (int i = TOTAL_NUM; i < TOTAL_NUM*2; i++) {

                HtmlListItem htmlListItem = (HtmlListItem) items.get(i);

                HtmlSpan htmlSpan = (HtmlSpan) htmlListItem.getElementsByTagName("span").get(1);
                weathers.add(htmlSpan.getFirstChild().toString());

                htmlSpan = (HtmlSpan) htmlListItem.getElementsByTagName("span").get(2);
                temps.add(htmlSpan.getFirstChild().toString());
            }

        } catch (IOException e) {
            System.err.println("error");
            e.printStackTrace();
            return false;
        }
        webClient.close();
        return true;
    }

    /**
     * Get weather type of an hour in the future within 24 hours.
     * @param index
     * @return
     */
    public String getWeather(int index){
        return this.weathers.get(index);
    }

    /**
     * Get accurate time of an hour in the future within 24 hours.
     * @param index
     * @return
     */
    public String getTime(int index){
        return this.times.get(index);
    }

    /**
     * Get temperature of an hour in the future within 24 hours.
     * @param index
     * @return
     */
    public String getTemperature(int index){
        return this.temps.get(index);
    }

    public boolean getResult(){
        return this.isSuccessful;
    }

    public Set<String> getWeatherSet(){
        return new HashSet(weathers);
    }

    public Set<String> getTimeSet(){
        return new HashSet(times);
    }

    public Set<String> getTemperatureSet(){
        return new HashSet(temps);
    }

}