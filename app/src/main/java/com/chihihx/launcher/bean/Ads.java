package com.chihihx.launcher.bean;

public class Ads {

    private int spanSize = 1;
    private final int picture;

    public Ads(int spanSize, int picture){
        this.spanSize = spanSize;
        this.picture = picture;
    }

    public int getPicture() {
        return picture;
    }

    public int getSpanSize(){
        return spanSize;
    }
}
