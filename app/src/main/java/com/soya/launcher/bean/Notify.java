package com.soya.launcher.bean;

public class Notify {
    public static final int TYPE_BLUETOOTH = 0;
    public static final int TYPE_AP = 1;
    public static final int TYPE_USB = 2;

    private int type;
    private final int icon;

    public Notify(int icon){
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }
}
