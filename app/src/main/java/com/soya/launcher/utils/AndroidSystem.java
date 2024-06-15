package com.soya.launcher.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.role.RoleManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.role.RoleManagerCompat;

import com.google.gson.Gson;
import com.open.system.SystemUtils;
import com.open.system.bean.StorageVolumeInfo;
import com.soya.launcher.BuildConfig;
import com.soya.launcher.R;
import com.soya.launcher.bean.AppPackage;
import com.soya.launcher.bean.HomeItem;
import com.soya.launcher.bean.Version;
import com.soya.launcher.config.Config;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.enums.Types;
import com.soya.launcher.ui.activity.MainActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AndroidSystem {
    public static boolean isSdCardAvaiable(Context context){
        List<StorageVolumeInfo> infos = SystemUtils.getVolumeInfos(context);
        return !infos.isEmpty();
    }

    public static boolean isEthernetConnected(Context context){
        ConnectivityManager cm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            cm = context.getSystemService(ConnectivityManager.class);
        }else{
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return info != null && info.isConnectedOrConnecting() && info.isAvailable();
    }

    public static void openSystemSetting2(Context context){
        try {
            openActivityName(context, "com.android.tv.settings", "com.android.tv.settings.MainSettings");

        }catch (Exception e){
            AndroidSystem.openSystemSetting(context);
        }
    }

    public static void requestInstallApk(Context context){
        Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static PackageInfo findPackageInfo(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : packageList) {
            if (packageName.equals(packageInfo.packageName)){
                return packageInfo;
            }
        }
        return null;
    }

    public static String getDeviceId(Context context){
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static Bitmap view2Bitmap(Context context, View dectorView){
        dectorView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(dectorView.getDrawingCache());
        dectorView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static void blur(Context context, View dectorView, View root, ImageView blur){
        Bitmap bitmap = view2Bitmap(context, dectorView);
        root.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams lp = blur.getLayoutParams();
                lp.width = root.getMeasuredWidth();
                lp.height = root.getMeasuredHeight();
                Bitmap bit = Bitmap.createBitmap(lp.width, lp.height, Bitmap.Config.RGB_565);
                Rect rect = new Rect();
                root.getGlobalVisibleRect(rect);
                blur.setLayoutParams(lp);
                Canvas canvas = new Canvas(bit);
                canvas.drawBitmap(bitmap, rect, new Rect(0, 0, bit.getWidth(), bit.getHeight()), new Paint());
                canvas.drawARGB(95 , 168, 168, 168);
                //bit = Bitmap.createScaledBitmap(bit, (int) (lp.width * 0.7f), (int) (lp.height * 0.7f), false);
                GlideUtils.bindBlur(context, blur, bit, blur.getDrawable(), 12, 8);
            }
        });
    }

    public static boolean isUsePassWifi(ScanResult bean){
        return AccessPointState.getScanResultSecurity(bean) != AccessPointState.OPEN;
    }

    public static Bitmap getImageForAssets(Context context, String filePath){
        Bitmap bitmap = null;
        AssetManager am = context.getAssets();
        try {
            InputStream in = am.open(filePath);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public static List<ResolveInfo> queryCategoryAllLauncher(Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
    }

    public static List<ResolveInfo> queryBrowareLauncher(Context context, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));

        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
    }

    public static List<PackageInfo> queryInstalledApps(Context context){
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> infos = packageManager.getInstalledPackages(0);
        return infos;
    }

    public static void openActivityInfo(Context context, ActivityInfo info){
        Intent intent = new Intent(Intent.ACTION_MAIN)
                .setClassName(info.applicationInfo.packageName, info.name)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Intent getPackageNameIntent(Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent == null) intent = pm.getLeanbackLaunchIntentForPackage(packageName);
        return intent;
    }

    public static boolean openPackageName(Context context, String packageName){
        Intent intent = getPackageNameIntent(context, packageName);
        if (intent == null){
            return false;
        }else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
    }

    public static boolean openActivityName(Context context, String packageName, String className){
        Intent intent = new Intent(Intent.ACTION_MAIN)
                .setClassName(packageName, className);
        context.startActivity(intent);
        return true;
    }

    public static boolean openActivityName(Context context, String name){
        boolean success = false;
        List<ResolveInfo> infos = AndroidSystem.queryCategoryAllLauncher(context);
        for (ResolveInfo info : infos) {
            if (info.activityInfo.name.equals(name)){
                AndroidSystem.openActivityInfo(context, info.activityInfo);
                success = true;
                break;
            }
        }
        return success;
    }

    public static boolean openActivityNames(Context context, String[] names){
        boolean success = false;
        List<ResolveInfo> infos = AndroidSystem.queryCategoryAllLauncher(context);
        outside:
        for (String name : names){
            for (ResolveInfo info : infos) {
                if (info.activityInfo.name.equals(name)){
                    AndroidSystem.openActivityInfo(context, info.activityInfo);
                    success = true;
                    break outside;
                }
            }
        }
        return success;
    }

    public static void openApplicationDetials(Context context, String packageName){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null));
        context.startActivity(intent);
    }

    public static void openWifiSetting(Context context){
        /*Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);*/
        openActivityName(context, "com.android.tv.settings", "com.android.tv.settings.connectivity.NetworkActivity");
    }

    public static void openBluSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        context.startActivity(intent);
    }

    public static void openWirelessSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        context.startActivity(intent);
    }

    public static boolean setDefaultLauncher(Context context, ActivityResultLauncher launcher){
        boolean success = false;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                RoleManager manager = context.getSystemService(RoleManager.class);
                launcher.launch(manager.createRequestRoleIntent(RoleManagerCompat.ROLE_HOME));
            }else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
                context.startActivity(intent);
            }
            success = true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return success;
        }
    }

    public static void openDateSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
        context.startActivity(intent);
    }

    public static void openVoiceSetting(Context context){
        try {
            Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
            context.startActivity(intent);
        }catch (Exception e){
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

    }

    public static void openInputSetting(Context context){
        /*Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        context.startActivity(intent);*/
        try {
            openActivityName(context, "com.android.tv.settings", "com.android.tv.settings.inputmethod.KeyboardActivity");

        }catch (Exception e){
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    public static void openBluetoothSetting(Context context){
        openActivityName(context, "com.android.tv.settings", "com.android.tv.settings.bluetooth.BluetoothActivity");
    }

    public static void openBluetoothSetting2(Context context){
        openActivityName(context, "com.android.tv.settings", "com.android.tv.settings.accessories.AddAccessoryActivity");
    }

    public static void openBluetoothSetting3(Context context){
        openActivityName(context, "com.android.tv.settings", "com.android.tv.settings.bluetooth.DevicePickerActivity");
    }

    public static void openBluetoothSetting4(Context context){
        openActivityName(context, "com.android.tv.settings",
                "com.android.tv.settings.bluetooth.BluetoothActivity");
    }


    public static boolean openLocalSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }

    public static boolean openSystemSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
        return true;
    }

    public static Bitmap getImageFromAssetsFile(Context context, String filePath) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(filePath);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String[] getAssetsFileNames(Context context, String filePath){
        String[] array = null;
        try {
            array = context.getAssets().list(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            return array;
        }
    }

    public static boolean isNetworkAvailable(Context context){
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnectedOrConnecting();

        return isNetworkConnected;
    }

    public static boolean jumpPlayer(Context context, AppPackage[] packages, String url){
        boolean success = false;
        List<ResolveInfo> infos = queryCategoryAllLauncher(context);
        for (AppPackage pk : packages){
            for (ResolveInfo resolveInfo : infos) {
                if (pk.getPackageName().equals(resolveInfo.activityInfo.packageName)){
                    success = true;
                    ActivityInfo info = resolveInfo.activityInfo;
                    Intent intent = getPackageNameIntent(context, info.packageName);
                    if (TextUtils.isEmpty(url)){
                        openPackageName(context, resolveInfo.activityInfo.packageName);
                    }else {
                        if (TextUtils.isEmpty(pk.getActivityName())){
                            if (intent != null){
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse(url));
                                context.startActivity(intent);
                            }
                        }else {
                            openPN(context, url, intent.getComponent().getPackageName(), pk.getActivityName());
                        }
                    }
                    return success;
                }
            }
        }

        return success;
    }

    public static void openPN(Context context, String url, String packageName, String className){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                .setClassName(packageName, className)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean jumpAppStore(Context context){
        return jumpAppStore(context, null, null);
    }

    public static boolean jumpAppStore(Context context, String data, String[] packageName){
        boolean success = false;
        List<ResolveInfo> infos = getAppStores(context);
        ResolveInfo defaultInfo = null;
        for (ResolveInfo resolveInfo : infos) {
            if (resolveInfo.activityInfo.packageName.equals(Config.STORE_PACKAGE_NAME)){
                if (TextUtils.isEmpty(data) && packageName == null) {
                    openActivityInfo(context, resolveInfo.activityInfo);
                }else {
                    ActivityInfo info = resolveInfo.activityInfo;
                    Intent intent = new Intent(Intent.ACTION_MAIN)
                            .setClassName(info.applicationInfo.packageName, Config.STORE_CLASS_NAME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(Atts.BEAN, data);
                    intent.putExtra(Atts.PACKAGE_NAME, packageName);
                    context.startActivity(intent);
                }
                success = true;
                break;
            }else {
                defaultInfo = resolveInfo;
            }
        }

        if (!success && defaultInfo != null && infos.size() != 0){
            success = true;
            openActivityInfo(context, infos.get(0).activityInfo);
        }
        return success;
    }

    public static void jumpUpgrade(Context context, Version version){
        try {
            version.setAppName(context.getString(R.string.app_name));
            version.setActivity(MainActivity.class.getName());
            version.setPackageName(BuildConfig.APPLICATION_ID);
            Gson gson = new Gson();
            PackageInfo info = findPackageInfo(context, Config.UPDATE_PACKAGE_NAME);
            if (info != null){
                Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(Config.UPDATE_PACKAGE_NAME, Config.UPDATE_CLASS_NAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Atts.BEAN, gson.toJson(version));
                context.startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean jumpVideoApp(Context context, AppPackage[] packages, String url){
        boolean success = false;
        success = jumpPlayer(context, packages, url);
        return success;
    }

    public static List<ResolveInfo> getAppStores(Context context){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MARKET);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        return infos;
    }

    public static void cleanFocus(View rootView){
        View v = rootView.findFocus();
        if (v != null){
            v.clearFocus();
        }
    }

    public static boolean isSystemApp(PackageInfo info) {
        boolean isSysApp = (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
        boolean isSysUpd = (info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
        return isSysApp | isSysUpd;
    }

    public static boolean isSystemApp(ResolveInfo info) {
        boolean isSysApp = (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
        boolean isSysUpd = (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
        return isSysApp || isSysUpd;
    }

    public static boolean openProjectorSetting(Context context){
        return AndroidSystem.openActivityNames(context, new String[]{
                "com.cptp.console.MainActivity",
                "com.softwinner.tcorrection.ScaleActivity",
        });
    }

    public static boolean openProjectorMode(Context context){
        return AndroidSystem.openActivityName(context,  "com.android.tv.settings", "com.android.tv.settings.display.ProjectionActivity");
    }

    public static boolean openProjectorHDMI(Context context){
        return openActivityNames(context, new String[]{"com.allwinner.hdmi.TVInputSetupActivity", "com.softwinner.awlivetv.MainActivity"});
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static String getSystemLanguage(Context context){
        Locale locale = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        }else {
            locale = Locale.getDefault();
        }
        return locale.getDisplayLanguage();
    }

    public static List<ApplicationInfo> getUserApps2(Context context){
        LauncherApps apps = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            apps = context.getSystemService(LauncherApps.class);
        }else {
            apps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        }
        List<LauncherActivityInfo> launchers = apps.getActivityList(null, android.os.Process.myUserHandle());

        Collections.sort(launchers, new Comparator<LauncherActivityInfo>() {
            @Override
            public int compare(LauncherActivityInfo o1, LauncherActivityInfo o2) {
                return (int) (o2.getFirstInstallTime() - o1.getFirstInstallTime());
            }
        });

        List<ApplicationInfo> result = new ArrayList<>();
        for (LauncherActivityInfo launcher : launchers){
            if (!launcher.getApplicationInfo().packageName.equals(BuildConfig.APPLICATION_ID)) result.add(launcher.getApplicationInfo());
        }
        return result;
    }

    public static boolean isSystemApp(int flags){
        return (flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public static List<ApplicationInfo> getUserApps(Context context){
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        List<ApplicationInfo> result = new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            Intent intent = getPackageNameIntent(context, packageInfo.packageName);
            if (intent != null && !packageInfo.packageName.equals(BuildConfig.APPLICATION_ID)) result.add(packageInfo);
        }
        Collections.sort(result, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                int value = 0;
                try {
                    PackageInfo info1 = pm.getPackageInfo(o1.packageName, 0);
                    PackageInfo info2 = pm.getPackageInfo(o2.packageName, 0);
                    value = (int) (info2.firstInstallTime - info1.firstInstallTime);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    return value;
                }
            }
        });
        return result;
    }

    public static void setSystemLanguage(Context context, Locale locale){
        SystemUtils.updateLocale(locale);
    }

    public static void setLanguageReflection(Context context, Locale locale){
        try {
            Class<?> c = Class.forName("com.android.internal.app.LocalePicker");
            Method method = c.getMethod("updateLocale", Locale.class);
            method.invoke(null, locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLanguageApp(Context context, Locale locale){
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        context.getResources().updateConfiguration(configuration, null);
    }

    public static Intent openWebUrl(String url){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return intent;
    }

    public static void setEnableBluetooth(Context context, boolean enable){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) return;
        if (!bluetoothAdapter.isEnabled() && enable){
            bluetoothAdapter.enable();
        }else if (bluetoothAdapter.isEnabled() && !enable){
            bluetoothAdapter.disable();
        }
        /*if (!bluetoothAdapter.isEnabled() && enable){
            bluetoothAdapter.enable();
        }else if (bluetoothAdapter.isEnabled() && !enable){
            bluetoothAdapter.disable();
        }*/
    }

    public static boolean connect(Context context, BluetoothProfile profile, BluetoothDevice device){
        boolean success = false;
        try {
            SystemUtils.removeBond(device);
            SystemClock.sleep(1000);
            device.createBond();
            SystemClock.sleep(1000);
            Class clz = Class.forName("android.bluetooth.BluetoothHidHost");
            Method connect = clz.getMethod("connect", BluetoothDevice.class);
            success = (boolean) connect.invoke(clz.cast(profile), device);
            SystemClock.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return success;
        }
    }

    public static <T> boolean uninstallPackage(Context context, String packageName) {
        boolean success = false;
        try {
            PackageManager packageManger = context.getPackageManager();
            PackageInstaller packageInstaller = packageManger.getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            params.setAppPackageName(packageName);
            int sessionId = packageInstaller.createSession(params);
            IntentSender sender = PendingIntent.getBroadcast(context, sessionId, new Intent(IntentAction.ACTION_DELETE_PACKAGE), PendingIntent.FLAG_MUTABLE).getIntentSender();
            packageInstaller.uninstall(packageName, sender);
            success = true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return success;
        }
    }

    public static void restoreFactory(Context context){
        openActivityName(context, "com.android.tv.settings","com.android.tv.settings.device.storage.ResetActivity");
    }
}
