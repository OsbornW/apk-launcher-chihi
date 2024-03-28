package com.soya.launcher.http.response;

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
        private Integer reg_id;

        public List<HomeItem> getMovies() {
            return movies;
        }



        public Integer getReg_id() {
            return reg_id;
        }
    }
}
