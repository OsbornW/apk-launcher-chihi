package com.soya.launcher.bean;

public class Wallpaper {
    private final int id;
    private final int picture;

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
