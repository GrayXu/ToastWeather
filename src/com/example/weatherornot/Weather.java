package com.example.weatherornot;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.util.Log;

public class Weather {
	private static String CharSet = "UTF-8";
	private String city;
	private JsonObject jsonOrinal;
	private JsonObject jsonData;
	private JsonArray jsonForecastArray;
	
	public Weather(String city) {
		this.city = city;
	}
	
	/**
	 * <p> change the word to UrlEncoded</p>
	 * @param word the word waiting to be urlencoded
	 * @return UrlEncoded string urlencoded
	 */
	static private String ToUrlEncoded(String word) {
		String urlStr = null;
		try {
			urlStr = URLEncoder.encode(word,CharSet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return urlStr;
	}
	
	
	/**
	 * <p> prepare JsonProject for outside using</p>
	 * @return 1:success -1:invalid city name 0:Error
	 */
	public int setJsonProject() {
		try {
			
			URL url = new URL("http://wthrcdn.etouch.cn/weather_mini?city="+ToUrlEncoded(this.city));
			URLConnection urlConnection = url.openConnection();
			
			//GZIP or not
			InputStream is;
			BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());  
			bis.mark(2);
			byte[] header = new byte[2];
			int result = bis.read(header);
			bis.reset();
			//Judge header
			int headerData = (int) ((header[0] << 8) | header[1] & 0xFF);
			if (result != -1 && headerData == 0x1f8b) {  
                is = new GZIPInputStream(bis);  
            } else {  
                is = bis;  
            }
			
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bReader = new BufferedReader(isr);
			
			StringBuilder sbWeather = new StringBuilder();
			String line;
			
			while((line = bReader.readLine()) != null){
				sbWeather.append(line);
			}
			bReader.close();
			
			JsonParser parser = new JsonParser();
			this.jsonOrinal = (JsonObject) parser.parse(sbWeather.toString());
			//check json data
			if(this.jsonOrinal.get("status").getAsInt()==1000){
				jsonData = this.jsonOrinal.get("data").getAsJsonObject();
				jsonForecastArray = jsonData.get("forecast").getAsJsonArray();
				return 1;
			}else{
				return -1;
			}
			
		} catch (MalformedURLException e) {
			Log.d("MalformedURLException", "MalformedURLException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("IOException", "IOException");
			e.printStackTrace();
		}
		return 0;
	}
	
	public String getAQI() {
		try{
			return this.jsonData.get("aqi").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "查询失败";
	}
	
	public String getTemperatureNow() {
		try{
			return this.jsonData.get("wendu").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "查询失败";
	}
	
	public String getCity() {
		try{
			return this.jsonData.get("city").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "查询失败";
	}
	
	public HashMap<String, String> getSomeDaydata(int day) {
		if(day >= 0 && day <=4){
			try {
				HashMap<String, String> day2Map = new HashMap<String, String>();
				JsonObject day2Object = this.jsonForecastArray.get(day).getAsJsonObject();
				day2Map.put("日期", day2Object.get("date").getAsString());
				day2Map.put("最高温", day2Object.get("high").getAsString());
				day2Map.put("最低温", day2Object.get("low").getAsString());
				day2Map.put("风力", day2Object.get("fengli").getAsString());
				day2Map.put("天气", day2Object.get("type").getAsString());
				return day2Map;
			} catch (Exception e) {
				Log.e("Weather Class", "Hashmap hasn't init");
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String getColdInfo(){
		try{
			return jsonData.get("ganmao").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
//	
//	public static void main(String[] args) {
//		System.out.print("输入需要查询的城市：");
//		Scanner input = new Scanner(System.in);
//		String inputCity = input.next();
//		input.close();
//		Weather weather = new Weather(inputCity);
//		if(weather.setJsonProject()==1){//Query Successfully
//			
//			System.out.println("查询的城市是："+ weather.getCity());
//			System.out.println("现在的温度："+ weather.getTemperatureNow());
//			System.out.println("AQI指数是："+ weather.getAQI());
//			System.out.println("明天的最高温是："+ weather.getSomeDaydata(0).get("最高温"));
//		}else{
//			System.out.println("输入有错误");
//		}
//	}
}
