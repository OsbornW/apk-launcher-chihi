package com.soya.launcher.bean;

public class Movice {
    public static final int PIC_ASSETS = 0;
    public static final int PIC_NETWORD = 1;
    public static final int PIC_PLACEHOLDING = 2;

    private int type;

    private String picture;
    private String url;
    private String title;
    private int picType = PIC_ASSETS;

    public Movice(int type, String url, String picture, int picType){
        this(type, "", url, picture, picType);
    }

    public Movice(int type, String title, String url, String picture, int picType){
        this.type = type;
        this.title = title;
        this.url = url;
        this.picture = picture;
        this.picType = picType;
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

    public String getPicture() {
        return picture;
    }

    public int getPicType() {
        return picType;
    }
}
