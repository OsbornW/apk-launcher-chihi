package com.soya.launcher.bean;

public class PlaceHolderBean {
    public String path;

    public PlaceHolderBean(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "PlaceHolderBean{" +
                "path='" + path + '\'' +
                '}';
    }
}
