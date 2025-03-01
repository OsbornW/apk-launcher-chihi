package com.chihihx.launcher.bean;

public class Notify {
    public static final int TYPE_UDisk = 0;
    public static final int TYPE_TF = 1;
    public static final int TYPE_AP = 2;
    public static final int TYPE_Bluetooth = 3;

    private final int type;
    private final int icon;

    public Notify(int icon, int type) {
        this.icon = icon;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getIcon() {
        return icon;
    }
}
