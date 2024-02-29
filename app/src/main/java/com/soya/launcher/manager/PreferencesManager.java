package com.soya.launcher.manager;

import com.soya.launcher.enums.Atts;
import com.soya.launcher.utils.PreferencesUtils;

import java.util.Locale;

public class PreferencesManager {
    public static final String getCityName(){
        return PreferencesUtils.getString(Atts.CITY, "Hong Kong");
    }

    public static final int isGuide(){
        return PreferencesUtils.getInt(Atts.IS_GUIDE, 0);
    }

    public static final int getWallpaper(){
        return PreferencesUtils.getInt(Atts.WALLPAPER, 0);
    }

    public static final String getLanguage(){
        return PreferencesUtils.getString(Atts.LANGUAGE, Locale.getDefault().toLanguageTag());
    }

    public static final String getUid(){
        return PreferencesUtils.getString(Atts.UID, "");
    }

    public static final boolean is24Display(){
        return PreferencesUtils.getBoolean(Atts.IS_24_DISPLAY, false);
    }
}
