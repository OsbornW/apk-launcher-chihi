package com.soya.launcher.bean;

public class WebItem {
    private int type = 0;
    private String name;
    private int icon;
    private String link;

    public WebItem(int type, String name, int icon, String link){
        this.type = type;
        this.name = name;
        this.icon = icon;
        this.link = link;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public String getLink() {
        return link;
    }
}
