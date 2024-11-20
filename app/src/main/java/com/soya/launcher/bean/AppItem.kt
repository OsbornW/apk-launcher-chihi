package com.soya.launcher.bean;

public class AppItem {
    public static final int ICON_TYPE_LOCAL = 1;
    public static final int ICON_TYPE_NETWORD = 0;
    public static final int STATU_IDLE = 0;
    public static final int STATU_DOWNLOADING = 1;
    public static final int STATU_DOWNLOAD_FAIL = 2;
    public static final int STATU_INSTALLING = 3;
    public static final int STATU_INSTALL_SUCCESS = 4;
    public static final int STATU_INSTALL_FAIL = 5;

    private String name;
    private int icon;
    private String mes;
    private int status = STATU_IDLE;

    private final int iconType = ICON_TYPE_NETWORD;
    private final int id = -1;
    private String appName;
    private String appIcon;
    private String appState;
    private String appSize;
    private String appDownLink;
    private String appImg1;
    private double score;
    private float progress;

    private String packageName;
    private String localIcon;

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

    public void setAppDownLink(String appDownLink) {
        this.appDownLink = appDownLink;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float getProgress() {
        return progress;
    }

    public String getLocalIcon() {
        return localIcon;
    }

    public void setLocalIcon(String localIcon) {
        this.localIcon = localIcon;
    }
}
