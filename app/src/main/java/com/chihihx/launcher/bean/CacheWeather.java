package com.chihihx.launcher.bean;

public class CacheWeather {
    private long time = -1;
    private WeatherData weather;

    public void setWeather(WeatherData weather) {
        time = System.currentTimeMillis();
        this.weather = weather;
    }

    public WeatherData getWeather() {
        return weather;
    }

    public long getTime() {
        return time;
    }
}
