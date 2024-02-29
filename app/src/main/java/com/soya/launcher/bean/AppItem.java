package com.soya.launcher.bean;

public class AppItem {
    private String name;
    private int icon;
    private String mes;

    private int id = -1;
    private String appName;
    private String appIcon;
    private String appState;
    private String appSize;
    private String appDownLink;
    private String appImg1;
    private double score;
    private String packageName;

    public AppItem(){}

    public AppItem(String name, int icon, String mes){
        this.name = name;
        this.icon = icon;
        this.mes = mes;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public String getMes() {
        return mes;
    }


    public String getPackageName() {
        return packageName;
    }

    public double getScore() {
        return score;
    }

    public String getAppImg1() {
        return appImg1;
    }

    public int getId() {
        return id;
    }

    public String getAppDownLink() {
        return appDownLink;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppSize() {
        return appSize;
    }

    public String getAppState() {
        return appState;
    }
}
