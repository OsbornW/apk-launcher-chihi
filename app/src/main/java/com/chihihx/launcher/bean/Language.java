package com.chihihx.launcher.bean;

import java.util.Locale;

public class Language {
    private final String name;
    private final String desc;
    private final Locale language;

    public Language(String name, String desc, Locale language){
        this.name = name;
        this.desc = desc;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Locale getLanguage() {
        return language;
    }
}
