package com.soya.launcher.config;

import com.soya.launcher.BuildConfig;

public class Config {
    public static String UPDATE_PACKAGE_NAME = "com.soya.launcher.upgrade";
    public static String UPDATE_CLASS_NAME = "com.soya.launcher.upgrade.UpgradeActivity";

    public static String STORE_PACKAGE_NAME = "com.soya.store";
    public static String STORE_CLASS_NAME = "com.soya.store.ui.activity.AppDetialActivity";

    //修改渠道信息，修改build.gradle
    //0、弘信 1、爱泊优投影仪 2、爱泊优TV 3、玥芯通(TV-X98K)
    public static final int COMPANY = Integer.parseInt(BuildConfig.COMPANY);
    public static String APPID = BuildConfig.APP_ID;
    public static final String USER_ID = BuildConfig.USER_ID;

    //弘信
//    manifestPlaceholders = [
//    company   : "0",
//    user_id   : "62",
//    appId     : "launcher",
//    channel   : "LA27001",
//    chihi_type: "H700",
//    model     : "001"
//            ]

    //爱泊优 投影仪
//    manifestPlaceholders = [
//    company   : "1",
//    user_id   : "62",
//    appId     : "launcher",
//    channel   : "LA22001",
//    chihi_type: "PROJECTOR_001",
//    model     : "001"
//            ]

    //爱泊优 TV(BOX)
//    manifestPlaceholders = [
//    company   : "2",
//    user_id   : "62",
//    appId     : "launcher",
//    channel   : "LA22001",
//    chihi_type: "TV_001",
//    model     : "001"
//            ]

    //玥芯通 X98K(BOX)
//    manifestPlaceholders = [
//    company   : "3",
//    user_id   : "62",
//    appId     : "launcher",
//    channel   : "LA23001",
//    chihi_type: "X98K",
//    model     : "001"
//            ]

    public static final String CHANNEL = BuildConfig.CHANNEL;
    public static final String CHIHI_TYPE = BuildConfig.CHIHI_TYPE;
    public static final String MODEL = BuildConfig.MODEL;
}
