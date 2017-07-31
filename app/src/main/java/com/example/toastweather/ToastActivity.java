package com.example.toastweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class ToastActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        SharedPreferences sharedPreference = getSharedPreferences("MainActivity", MODE_PRIVATE);
        String AQI = sharedPreference.getString("AQI", "null");
        String tempNow = sharedPreference.getString("temper", "null");
        String coldTips = sharedPreference.getString("today", "null");
        String day0 = sharedPreference.getString("day0info", "null");
        String day1 = sharedPreference.getString("day1info", "null");
        String city = sharedPreference.getString("newCity", "武汉");

        if (!tempNow.equals("null")) {
            StringBuilder sb = new StringBuilder();
            sb.append(city + "现在" + tempNow);
            if (!AQI.equals("null") && AQI.indexOf("失败") == -1) {
                sb.append(" AQI" + AQI);
            }
            sb.append("\n" + coldTips);

            sb.append("\n" + getWeatherEmoji(day0) + day0.replaceAll("\n", " ").replaceAll(" 最高温", " ").replaceAll("最低温", "~"));
            sb.append("\n" + getWeatherEmoji(day1) + day1.replaceAll("\n", " ").replaceAll(" 最高温", " ").replaceAll("最低温", "~"));

            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();//在show队列里叠加时间
        } else {
            Toast.makeText(this, "未初始化", Toast.LENGTH_LONG).show();
        }

        finish();
        super.onResume();
    }

    private String getEmojiStringByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    private String getWeatherEmoji(String info) {
        if (info.indexOf('云') != -1) {
            return getEmojiStringByUnicode(0x2601);
        } else if (info.indexOf('晴') != -1) {
            return getEmojiStringByUnicode(0x2600);
        } else if (info.indexOf('雷') != -1) {
            return getEmojiStringByUnicode(0x26A1);
        } else if (info.indexOf('雨') != -1) {
            return getEmojiStringByUnicode(0x2614);
        } else if (info.indexOf('雪') != -1) {
            return getEmojiStringByUnicode(0x26C4);
        }
        return getEmojiStringByUnicode(0x26C5);
    }
}
