package com.soya.launcher.bean;

import java.util.Map;

public class MoviceData {
    private String videoId;
    private Map<String, String> thumbnailMap;
    private String netflix_id;
    private String disney_id;
    private String large_image;
    private String main_img;
    private String max_id;
    private String default_image;
    private String hulu_id;
    private String prime_videoid;

    public String getPrime_videoid() {
        return prime_videoid;
    }

    public String getHulu_id() {
        return hulu_id;
    }

    public Map<String, String> getThumbnailMap() {
        return thumbnailMap;
    }

    public String getLarge_image() {
        return large_image;
    }

    public String getMain_img() {
        return main_img;
    }

    public String getNetflix_id() {
        return netflix_id;
    }

    public String getDisney_id() {
        return disney_id;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getMax_id() {
        return max_id;
    }

    public String getDefault_image() {
        return default_image;
    }
}
