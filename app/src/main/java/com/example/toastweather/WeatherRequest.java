package com.example.toastweather;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

public class WeatherRequest {
	private static String CharSet = "UTF-8";
	private String city;
	private JsonObject jsonOriginal;
	private JsonObject jsonData;
	private JsonArray jsonForecastArray;
	private int requestResult;

	public WeatherRequest(String city) {
		this.city = city;
		requestResult = setJsonProject();
	}

	/**
	 * get request result
	 * @return 1:success -1:invalid city name 0:Error
	 */
	public int getRequestResult() {
		return requestResult;
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
	private int setJsonProject() {
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

		/*Judge header*/
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
			this.jsonOriginal = (JsonObject) parser.parse(sbWeather.toString());
			//check json data
			if(this.jsonOriginal.get("status").getAsInt()==1000){
				jsonData = this.jsonOriginal.get("data").getAsJsonObject();
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

	/**
	 * <p> get AQI num</p>
	 * @return String AQI Data
	 */
	public String getAQI() {
		try{
			return this.jsonData.get("aqi").getAsString();
		} catch (Exception e) {
			Log.e("getAQI","获取失败");
		}

		return "查询失败";
	}

	/**
	 * <p> get Temperature Now</p>
	 * @return String Temperature right now as ℃
	 */
	public String getTemperatureNow() {
		try{
			return this.jsonData.get("wendu").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "查询失败";
	}

	/**
	 * <p> get City's name</p>
	 * @return String City name
	 */
	public String getCity() {
		try{
			return this.jsonData.get("city").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "查询失败";
	}

	/**
	 *	Get weather forecast information form today to the 4th day afterwards.
	 * @param day From today to the fourth day afterwards(0-4)
	 * @return a HashMap with weather forecast information
	 */
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
				Log.e("WeatherRequest Class", "Hashmap hasn't init");
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * get tips about cold
	 * @return String those tips(Simplified Chinese)
	 */
	public String getColdTips(){
		try{
			return jsonData.get("ganmao").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>Get a weather forecast information in text of 5 days in the future including highest and lowest
	 * temperature, date, wind grading, weather type</p>
	 * @param day From today to the fourth day afterwards(0-4)
	 * @return String weather forecast in string
	 */
	public String getSomeDayInfo(int day){
		HashMap<String,String> hashMap = getSomeDaydata(day);
		StringBuilder sb = new StringBuilder();
		sb.append(hashMap.get("日期")+" "+hashMap.get("天气")+"\n");
		sb.append("最"+hashMap.get("最高温")+"\n"+"最"+hashMap.get("最低温"));
		sb.append("\n风力:"+hashMap.get("风力"));

		return sb.toString();
	}

}
