package com.soya.launcher.bean;

public class TypeItem {
    private String name;
    private int picture;
    private int type;
    private int color;

    public TypeItem(String name, int picture, int type){
        this(name, picture, type, -1);
    }

    public TypeItem(String name, int picture, int type, int color){
        this.name = name;
        this.picture = picture;
        this.type = type;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getPicture() {
        return picture;
    }

    public int getType() {
        return type;
    }
}
