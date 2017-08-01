package com.example.toastweather.Support;

/**
 * Created by Xgl on 2017/7/15.
 */

public class Weather {

    private int imageID;
    private String info;

    public Weather(int imageID, String info) {
        this.imageID = imageID;
        this.info = info;
    }

    public int getImageID() {
        return imageID;
    }

    public String getInfo() {
        return info;
    }
}
