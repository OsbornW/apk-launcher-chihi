package com.soya.launcher.http.response;

import com.soya.launcher.bean.HomeItem;

import java.util.List;

public class HomeResponse {
    private Inner data;

    public Inner getData() {
        return data;
    }

    public static class Inner{
        private List<HomeItem> movies;

        public List<HomeItem> getMovies() {
            return movies;
        }
    }
}
