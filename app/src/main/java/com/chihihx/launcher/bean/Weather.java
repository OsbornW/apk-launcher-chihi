package com.chihihx.launcher.bean;

public class Weather {
    private String dateDes;
    private String datetime;
    private double tempmax;
    private double tempmin;
    private String description;

    private String icon;

    private Weatherhour[] hours;

    public Weather(String dateDes){
        this.dateDes = dateDes;
    }

    public Weatherhour[] getHours() {
        return hours;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDateDes(String dateDes) {
        this.dateDes = dateDes;
    }

    public String getDateDes() {
        return dateDes;
    }

    public double getTempmax() {
        return tempmax;
    }

    public double getTempmin() {
        return tempmin;
    }

    public String getDatetime() {
        return datetime;
    }
}
