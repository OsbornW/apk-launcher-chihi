package com.chihihx.launcher.bean;

import java.util.TimeZone;

public class SimpleTimeZone {
    private final TimeZone zone;
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
