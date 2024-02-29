package com.soya.launcher.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.soya.launcher.BuildConfig;
import com.soya.launcher.enums.Atts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtils {
    public static void installApk(Context context, String filePath) {
        Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider", new File(filePath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void adbInstallApk(String filePath) throws Exception {
        String cmd = "pm install -r "+filePath+" & reboot";
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
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

    public static boolean is24Display(Context context){
        return "24".equals(Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24));
    }

    public static void setTime(long timeMillis){
        SystemClock.setCurrentTimeMillis(timeMillis);
    }
}
