package com.soya.launcher.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import com.soya.launcher.BuildConfig;

import java.io.File;

public class AppUtils {
    public static void installApk(Context context, String filePath) {
        Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider", new File(filePath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static int adbInstallApk(String filePath) throws Exception {
        String cmd = "pm install -r "+filePath;
        Process process = Runtime.getRuntime().exec(cmd);
        int code = process.waitFor();
        process.destroy();
        return code;
    }

    public static void adbUninstallApk(String packageName) throws Exception {
        String cmd = "pm uninstall " + packageName;
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        process.destroy();
    }

    public static void openHDMI() throws Exception {
        String cmd = "am broadcast -a android.cvim.action.HDMI.SOURCE ";
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        process.destroy();
    }

    public static void restart() throws Exception {
        Process process = Runtime.getRuntime().exec("reboot");
        process.waitFor();
    }

    public static void setAutoDate(Context context, boolean isAuto){
        try {
            Settings.Global.putInt(context.getContentResolver(), Settings.Global.AUTO_TIME, isAuto ? 1 : 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean set24Display(Context context, boolean is24Display){
        return Settings.System.putString(context.getContentResolver(), Settings.System.TIME_12_24, is24Display ? "24" : "12");
    }

    public static boolean setTimeZone(Context context, String zone){
        return Settings.System.putString(context.getContentResolver(),  Settings.Global.AUTO_TIME_ZONE, zone);
    }

    public static boolean is24Display(Context context){
        return "24".equals(Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24));
    }

    public static void setTime(long timeMillis){
        SystemClock.setCurrentTimeMillis(timeMillis);
    }
}
