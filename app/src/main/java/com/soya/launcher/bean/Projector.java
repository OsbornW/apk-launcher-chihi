package com.soya.launcher.bean;

public class Projector {
    public static final int TYPE_SETTING = 0;
    public static final int TYPE_HDMI = 1;
    public static final int TYPE_SCREEN = 2;
    public static final int TYPE_PROJECTOR_MODE = 3;

    private int type;
    private int icon;

    public Projector(int type, int icon){
        this.type = type;
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public int getIcon() {
        return icon;
    }
}
