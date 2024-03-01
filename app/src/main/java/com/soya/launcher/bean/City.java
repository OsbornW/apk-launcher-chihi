package com.soya.launcher.bean;

public class City {
    private String CityName;
    private String Latitude;
    private String Longitude;
    private String ISO_Code;
    private String Timezone;

    public String getCityName() {
        if (CityName == null) CityName = "";
        return CityName;
    }

    public String getISO_Code() {
        if (ISO_Code == null) ISO_Code = "";
        return ISO_Code;
    }

    public String getLatitude() {
        if (Latitude == null) Latitude = "";
        return Latitude;
    }

    public String getLongitude() {
        if (Longitude == null) Longitude = "";
        return Longitude;
    }

    public String getTimezone() {
        return Timezone;
    }
}
