package com.soya.launcher.config;

import com.soya.launcher.BuildConfig;

public class Config {
    public static String PACKAGE_NAME = "com.soya.launcher.upgrade";
    public static String CLASS_NAME = "com.soya.launcher.upgrade.UpgradeActivity";

    //修改渠道信息，修改build.gradle
    //0、弘信 1、爱泊优投影仪 2、爱泊优TV 3、玥芯通(TV-X98K)
    public static final int COMPANY = Integer.parseInt(BuildConfig.COMPANY);
    public static String APPID = BuildConfig.APP_ID;
    public static final String USER_ID = BuildConfig.USER_ID;

    //弘信
//    public static final String CHANNEL = "LA27001";
//    public static final String CHIHI_TYPE = "H700";
//    public static final String MODEL = "cupid";

    //爱泊优 投影仪
//    public static final String CHANNEL = "LA22001";
//    public static final String CHIHI_TYPE = "PROJECTOR_001";
//    public static final String MODEL = "001";

    //爱泊优 TV(BOX)
//    public static final String CHANNEL = "LA22001";
//    public static final String CHIHI_TYPE = "TV_001";
//    public static final String MODEL = "001";

    //玥芯通 X98K(BOX)
//    public static final String CHANNEL = "LA23001";
//    public static final String CHIHI_TYPE = "X98K";
//    public static final String MODEL = "001";

    public static final String CHANNEL = BuildConfig.CHANNEL;
    public static final String CHIHI_TYPE = BuildConfig.CHIHI_TYPE;
    public static final String MODEL = BuildConfig.MODEL;
}
