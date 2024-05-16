package com.soya.launcher.bean;

public class AboutItem {
    private final int type;
    private final int icon;
    private final String title;
    private String description;

    public AboutItem(int type, int icon, String title, String description){
        this.type = type;
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public int getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
}
