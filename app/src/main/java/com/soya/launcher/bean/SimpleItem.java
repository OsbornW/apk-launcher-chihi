package com.soya.launcher.bean;

public class SimpleItem {
    private int type;
    private String title;
    private String desc;

    public SimpleItem(int type, String title, String desc){
        this.type = type;
        this.title = title;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
