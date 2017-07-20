package com.example.toastweather;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Xgl on 2017/7/19.
 * 天气预报recycler view的适配器
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.myViewHolder> {

    private List<Weather> weatherList;


    static class myViewHolder extends RecyclerView.ViewHolder {
        View weatherView;
        ImageView weatherImage;
        TextView weatherInfo;

        public myViewHolder(View itemView) {
            super(itemView);
            weatherView = itemView;
            weatherImage = (ImageView) itemView.findViewById(R.id.weather_image);
            weatherInfo = (TextView) itemView.findViewById(R.id.weather_info);
        }
    }

    public WeatherAdapter(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        final myViewHolder holder = new myViewHolder(view);

        //recycler view 的监听器填装
        holder.weatherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
//                Weather weather = weatherList.get(position);
                Log.d("按键事件","You Clicked day " + (position+1));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        holder.weatherImage.setImageResource(weather.getImageID());
        holder.weatherInfo.setText(weather.getInfo());
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }
}
