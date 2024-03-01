package com.soya.launcher.http;

public class Url {
    public static final String YOUTUBE_PLAY = "https://www.youtube.com/watch?v=%s";
    public static final String NETFLIX_PLAY = "http://www.netflix.com/watch/%s";
    public static final String DISNEY_PLAY = "https://www.disneyplus.com/movies/%s";
    public static final String HULU_PLAY = "https://www.hulu.com/%s";
    public static final String MAX_PLAY = "https://play.max.com/%s";
    public static final String PRIME_VIDEO_PLAY = "https://www.amazon.com/%s";
    public static final String BASE_URL = "https://99soya.shop/";
    public static final String APP_STORE_BASE_URL = "https://appzones.info/";
    public static final String UID_BASE_URL = "http://launcher.swifttvs.com/";
    public static final String MOVICE_LIST = "wheateapi/getallvideodata";
    public static final String BASE_RECOMMEND_LIST = "https://www.googleapis.com/";
    public static final String RECOMMEND_LIST = "youtube/v3/videos?part=snippet,contentDetails,statistics&chart=mostPopular&key=AIzaSyCaXBz_x3Euwc_a_xUykF1jhg1nNcSuzH0&maxResults=20";

    public static final String SEARCH_LIST = "youtube/v3/videos?part=snippet,contentDetails,statistics&chart=mostPopular&key=AIzaSyCaXBz_x3Euwc_a_xUykF1jhg1nNcSuzH0&maxResults=512";

    public static final String HOT_CITY = "wheateapi/getHotCities";
    public static final String SEARCH_CITY = "wheateapi/getcitys";
    public static final String CITY_WEATHER = "wheateapi/getCityData";

    public static final String CHECK_VERSION = "appapi/appinfo/getVersion";
    public static final String UID_PULL = "api/v1/app-callback-info";
    public static final String APP_LIST = "cj/carApp/getAppListTV";

    public static String conformity(String url, String id){
        return String.format(url, id);
    }
}
