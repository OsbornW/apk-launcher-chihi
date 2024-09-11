package com.soya.launcher.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import java.lang.reflect.Method;

public class SystemUtils {

    public static boolean isApEnable(Context context) {
        try {
            // 获取 WifiManager 实例
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            // 反射获取 isWifiApEnabled 方法
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            // 调用该方法并返回结果
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
