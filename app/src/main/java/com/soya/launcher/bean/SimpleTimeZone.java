package com.soya.launcher.bean;

import java.sql.Time;
import java.util.TimeZone;

public class SimpleTimeZone {
    private TimeZone zone;
    private final String name;
    private final String desc;

    public SimpleTimeZone(TimeZone zone, String name, String desc){
        this.zone = zone;
        this.name = name;
        this.desc = desc;
    }

    public TimeZone getZone() {
        return zone;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}