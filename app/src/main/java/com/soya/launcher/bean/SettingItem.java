package com.soya.launcher.bean;

public class SettingItem {
    private int type;
    private String name;
    private int ico;

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

    public void setIco(int ico) {
        this.ico = ico;
    }

    public SettingItem setName(String name) {
        this.name = name;
        return this;
    }

    public SettingItem setType(int type) {
        this.type = type;
        return this;
    }
}
