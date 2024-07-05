package com.soya.launcher.bean;

import java.util.List;

public class DivSearch<T> {
    private final int type;
    private final String title;
    private final List<T> list;
    private int state;

    public DivSearch(int type, String title, List<T> list, int state){
        this.type = type;
        this.title = title;
        this.list = list;
        this.state = state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public List<T> getList() {
        return list;
    }
}
