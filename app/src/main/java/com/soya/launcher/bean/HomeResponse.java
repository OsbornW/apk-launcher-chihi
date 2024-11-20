package com.soya.launcher.bean;

import com.soya.launcher.bean.HomeItem;

import java.util.List;

public class HomeResponse {
    public Inner data;

    public void setData(Inner data) {
        this.data = data;
    }

    public Inner getData() {
        return data;
    }

    public static class Inner{
        public List<HomeItem> movies;
        private int req_id;

        public List<HomeItem> getMovies() {
            return movies;
        }



        public int getReg_id() {
            return req_id;
        }
    }
}
