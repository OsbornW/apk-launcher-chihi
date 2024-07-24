package com.soya.launcher.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Movice implements Serializable {
    public static final int PIC_ASSETS = 0;
    public static final int PIC_NETWORD = 1;
    public static final int PIC_PLACEHOLDING = 2;

    private boolean isLocal;
    private final int type;
    private Object imageUrl;
    private String imageName;
    private String picture;
    private final String url;
    private final String title;
    private String appName;
    private String id;
    private AppPackage[] appPackage;
    private int picType = PIC_ASSETS;
    private List<PlaceHolderBean> placeHolderList;

    public Movice(int type, String url, String imageUrl, int picType){
        this(type, "", url, imageUrl, picType);
    }

    public Movice(int type, String title, String url, String imageUrl, int picType){
        this.type = type;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.picType = picType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAppPackage(AppPackage[] appPackage) {
        this.appPackage = appPackage;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setPicType(int picType) {
        this.picType = picType;
    }

    public String getAppName() {
        return appName;
    }

    public String getPicture() {
        return picture;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public AppPackage[] getAppPackage() {
        return appPackage;
    }

    public Object getImageUrl() {
        return imageUrl;
    }

    public int getPicType() {
        return picType;
    }

    public void setImageUrl(Object imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public List<PlaceHolderBean> getPlaceHolderList() {
        return placeHolderList;
    }

    public void setPlaceHolderList(List<PlaceHolderBean> placeHolderList) {
        this.placeHolderList = placeHolderList;
    }

    public Object getImageName() {
        if (imageName == null||imageName.isEmpty()) {
            return "icon_media_center";
        }
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "Movice{" +
                "isLocal=" + isLocal +
                ", type=" + type +
                ", imageUrl=" + imageUrl +
                ", picture='" + picture + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", appName='" + appName + '\'' +
                ", appPackage=" + Arrays.toString(appPackage) +
                ", picType=" + picType +
                '}';
    }
}
