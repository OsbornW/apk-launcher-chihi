package com.chihihx.launcher.bean;

public class SimpleItem {
    private final int type;
    private final String title;
    private final String desc;

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
