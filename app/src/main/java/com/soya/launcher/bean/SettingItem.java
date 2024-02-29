package com.soya.launcher.bean;

public class SettingItem {
    private final int type;
    private final String name;
    private final int ico;

    public SettingItem(int type, String name, int ico){
        this.type = type;
        this.name = name;
        this.ico = ico;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getIco() {
        return ico;
    }
}
