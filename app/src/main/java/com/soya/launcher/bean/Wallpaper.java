package com.soya.launcher.bean;

public class Wallpaper {
    private int id;
    private int picture;

    public Wallpaper(int id, int picture){
        this.id = id;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public int getPicture() {
        return picture;
    }
}
