package com.soya.launcher.bean;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.WindowManager;

import com.open.system.ASystemProperties;
import com.open.system.SystemUtils;
import com.soya.launcher.BuildConfig;
import com.soya.launcher.R;
import com.soya.launcher.manager.PreferencesManager;
import com.soya.launcher.utils.AndroidSystem;

public class AppInfo {
    private String appName;
    private String appQdName;
    private String appVersion;
    private String mac;
    private String ip;
    private String sdkVersion;
    private String imei;
    private String randomStr;

    public static AppInfo newInfo(Context context){
        String[] macs = SystemUtils.getMAC(context);
        WifiManager wifiManager = context.getSystemService(WifiManager.class);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        AppInfo info = new AppInfo();
        info.setAppName(context.getString(R.string.app_name));
        info.setAppVersion(BuildConfig.VERSION_NAME);
        info.setIp(Formatter.formatIpAddress(wifiInfo.getIpAddress()));
        info.setMac(macs != null && macs.length != 0 ? macs[0] : "");
        info.setImei(AndroidSystem.getDeviceId(context));
        info.setSdkVersion(String.valueOf(Build.VERSION.SDK_INT));
        info.setAppQdName(Build.MODEL);
        info.setRandomStr(PreferencesManager.getUid());
        return info;
    }

    public String getAppName() {
        return appName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppQdName() {
        return appQdName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getImei() {
        return imei;
    }

    public String getMac() {
        return mac;
    }

    public String getRandomStr() {
        return randomStr;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppQdName(String appQdName) {
        this.appQdName = appQdName;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setRandomStr(String randomStr) {
        this.randomStr = randomStr;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }
}
