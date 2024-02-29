package com.soya.launcher.manager;

import android.content.Context;

public class FilePathMangaer {
    public static final String getLocalNetflixPictures(Context context){
        return "picture/netflix";
    }

    public static final String getLocalYouTubePictures(Context context){
        return "picture/youtube";
    }

    public static final String getJsonPath(Context context){
        return context.getFilesDir().getAbsolutePath();
    }

    public static final String getWeatherIcon(String fileName){
        return "weather/"+fileName+".png";
    }

    public static final String getAppUpgradePath(Context context){
        return context.getFilesDir().getAbsolutePath();
    }

    public static final String getLocalDisneyPictures(Context context){
        return "picture/disney";
    }
}
