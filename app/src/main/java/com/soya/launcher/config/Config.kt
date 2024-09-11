package com.soya.launcher.config;

import com.soya.launcher.BuildConfig;

public class Config {
    public static String UPDATE_PACKAGE_NAME = "com.soya.launcher.upgrade";
    public static String UPDATE_CLASS_NAME = "com.soya.launcher.upgrade.UpgradeActivity";

    public static String STORE_PACKAGE_NAME = "com.soya.store";
    public static String STORE_CLASS_NAME = "com.soya.store.ui.activity.AppDetailActivity";

    //修改渠道信息，修改build.gradle
    //0、弘信 1、爱泊优投影仪 2、爱泊优TV 3、玥芯通(TV-X98K)
    public static final int COMPANY = Integer.parseInt(BuildConfig.COMPANY);
    public static String APPID = BuildConfig.APP_ID;
    public static final String USER_ID = BuildConfig.USER_ID;
    public static final String CHANNEL = BuildConfig.CHANNEL;
    public static final String CHIHI_TYPE = BuildConfig.CHIHI_TYPE;
    public static final String MODEL = BuildConfig.MODEL;
}
