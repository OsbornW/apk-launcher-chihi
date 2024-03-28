package com.soya.launcher;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.bean.CacheWeather;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.bean.MyRunnable;
import com.soya.launcher.bean.PushApp;
import com.soya.launcher.bean.UDPMessage;
import com.soya.launcher.bean.Wallpaper;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.manager.FilePathMangaer;
import com.soya.launcher.manager.PreferencesManager;
import com.soya.launcher.manager.UDPServer;
import com.soya.launcher.service.AppService;
import com.soya.launcher.service.OutsideBroadcast;
import com.soya.launcher.ui.dialog.RemoteDialog;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.AppUtils;
import com.soya.launcher.utils.BluetoothScannerUtils;
import com.soya.launcher.utils.FileUtils;
import com.soya.launcher.utils.PreferencesUtils;

import java.io.File;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;

public class App extends Application {
    private static App instance;
    private static final Gson GSON = new Gson();
    public static final List<AppItem> PUSH_APPS = new CopyOnWriteArrayList<>();
    private static final ExecutorService exec = Executors.newCachedThreadPool();
    public static final Map<Long, List<Movice>> MOVIE_MAP = new ConcurrentHashMap<>();
    public static final Map<String, Object> MOVIE_IMAGE = new ConcurrentHashMap<>();
    public static final CacheWeather WEATHER = new CacheWeather();
    public static final List<Wallpaper> WALLPAPERS = new ArrayList<>();
    public static final List<AppItem> APP_STORE_ITEMS = new CopyOnWriteArrayList<>();
    public static boolean useRemoteDialog = true;

    private InnerReceiver receiver;
    private RemoteDialog mFailDialog;
    private RemoteDialog mSuccessDialog;
    private MyRunnable remoteRunnable;
    private MyRunnable downloadRunnable;
    private MyRunnable installRunnable;
    private long lastRemoteTime = -1;

    private UDPServer server;
    private boolean isSkipDonwload;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        OkGo.init(this);
        HttpRequest.init(this);
        PreferencesUtils.init(this);

        if (TextUtils.isEmpty(PreferencesUtils.getString(Atts.UID, ""))){
            PreferencesUtils.setProperty(Atts.UID, UUID.randomUUID().toString());
        }
        BluetoothScannerUtils.init(this);
        initRemote();

        WALLPAPERS.add(new Wallpaper(0, R.drawable.wallpaper_1));
        WALLPAPERS.add(new Wallpaper(1, R.drawable.wallpaper_20));
        WALLPAPERS.add(new Wallpaper(2, R.drawable.wallpaper_21));
        WALLPAPERS.add(new Wallpaper(3, R.drawable.wallpaper_22));
        WALLPAPERS.add(new Wallpaper(4, R.drawable.wallpaper_24));
        WALLPAPERS.add(new Wallpaper(5, R.drawable.wallpaper_25));

        com.hs.App.init(this);

        AndroidSystem.setEnableBluetooth(this, true);
        udp();
        timeRemote();
        downloadRunnable();

        if (PreferencesManager.getLastVersionCode() != BuildConfig.VERSION_CODE){
            try {
                FileUtils.copyAssets(getAssets(), "movies", getFilesDir());
                PreferencesUtils.setProperty(Atts.LAST_VERSION_CODE, BuildConfig.VERSION_CODE);
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }
        }
    }

    private void timeRemote(){
        if (remoteRunnable != null) remoteRunnable.interrupt();
        remoteRunnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    if (lastRemoteTime == -1) continue;
                    if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastRemoteTime) >= 5){
                        lastRemoteTime = System.currentTimeMillis();
                        if (mFailDialog.isShowing()) mFailDialog.dismiss();
                        if (mSuccessDialog.isShowing()) mSuccessDialog.dismiss();
                    }
                }
            }
        };
        exec.execute(remoteRunnable);
    }

    private void udp(){
        if (server != null) server.destory();
        server = new UDPServer();
        server.setCallback(new UDPServer.Callback() {
            @Override
            public void callback(String data, InetAddress address, int port) {
                UDPMessage message = GSON.fromJson(data, UDPMessage.class);
                switch (message.type){
                    case UDPMessage.TYPE_DOWNLOAD_APK:{
                        AppItem item = GSON.fromJson(message.data, AppItem.class);
                        if (item != null) {
                            boolean isAdd = true;
                            for (AppItem child : PUSH_APPS){
                                if (child.getAppDownLink().equals(item.getAppDownLink())){
                                    if (child.getStatus() == AppItem.STATU_DOWNLOAD_FAIL || child.getStatus() == AppItem.STATU_INSTALL_FAIL) {
                                        child.setStatus(AppItem.STATU_IDLE);
                                        child.setProgress(0f);
                                    }
                                    isAdd = false;
                                    break;
                                }
                            }
                            if (isAdd) PUSH_APPS.add(item);
                        }
                    }
                    break;
                    case UDPMessage.TYPE_GET_PACKAGE_MESSAGE:{
                        boolean isFind = false;
                        for (AppItem child : PUSH_APPS){
                            if (child.getAppDownLink().equals(message.data)){
                                isFind = true;
                                server.send(GSON.toJson(new UDPMessage(UDPMessage.TYPE_RESPONSE_PACKAGE_MESSAGE, GSON.toJson(child))).getBytes(StandardCharsets.UTF_8), address, port);
                                break;
                            }
                        }
                        if (!isFind) server.send(GSON.toJson(new UDPMessage(UDPMessage.TYPE_RESPONSE_PACKAGE_MESSAGE, null)).getBytes(StandardCharsets.UTF_8), address, port);
                    }
                    break;
                }
            }
        });
        exec.execute(server);
    }

    private void downloadRunnable(){
        if (downloadRunnable != null) downloadRunnable.interrupt();
        downloadRunnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    if (isSkipDonwload || PUSH_APPS.isEmpty()) continue;
                    AppItem item = null;
                    for (AppItem child : PUSH_APPS){
                        if (child != null && child.getStatus() == AppItem.STATU_IDLE){
                            item = child;
                            break;
                        }
                    }

                    if (item != null){
                        downloadApk(item);
                    }
                    SystemClock.sleep(1000);
                }
            }
        };
        exec.execute(downloadRunnable);
    }

    private void downloadApk(final AppItem bean){
        isSkipDonwload = true;
        bean.setStatus(AppItem.STATU_DOWNLOADING);
        OkGo.get(bean.getAppDownLink()).execute(new FileCallback(FilePathMangaer.getAppDownload(this), bean.getName()+".apk") {
            @Override
            public void onError(okhttp3.Call call, Response response, Exception e) {
                bean.setStatus(AppItem.STATU_DOWNLOAD_FAIL);
                isSkipDonwload = false;
            }

            @Override
            public void onSuccess(File file, okhttp3.Call call, Response response) {
                bean.setProgress(1f);
                silentInstall(bean, file);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                bean.setStatus(AppItem.STATU_DOWNLOADING);
                bean.setProgress(progress);
            }
        });
    }

    private void silentInstall(AppItem bean, final File file){
        bean.setStatus(AppItem.STATU_INSTALLING);
        if (installRunnable != null) installRunnable.interrupt();
        installRunnable = new MyRunnable() {
            @Override
            public void run() {
                try {
                    int code = AppUtils.adbInstallApk(file.getAbsolutePath());
                    bean.setStatus(code == 0 ? AppItem.STATU_INSTALL_SUCCESS : AppItem.STATU_INSTALL_FAIL);
                }catch (Exception e){
                    bean.setStatus(AppItem.STATU_INSTALL_FAIL);
                    e.printStackTrace();
                }finally {
                    isSkipDonwload = false;
                    if (file.exists()) file.delete();
                }
            }
        };
        exec.execute(installRunnable);
    }

    private void initRemote(){
        mFailDialog = new RemoteDialog(this, R.layout.dialog_blu_fail);
        mSuccessDialog = new RemoteDialog(this, R.layout.dialog_blu_success);
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(IntentAction.ACTION_NOT_SHOW_REMOTE_DIALOG);
        filter.addAction(IntentAction.ACTION_SHOW_REMOTE_DIALOG);
        registerReceiver(receiver, filter);
    }

    public class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (intent.getAction()){
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) break;
                    if (!mSuccessDialog.isShowing() && canDrawOverlays() && useRemoteDialog) mSuccessDialog.show();
                    if (mFailDialog.isShowing()) mFailDialog.dismiss();
                    mSuccessDialog.setName(device.getName());
                    lastRemoteTime = System.currentTimeMillis();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) break;
                    if (!mFailDialog.isShowing() && canDrawOverlays() && useRemoteDialog) mFailDialog.show();
                    if (mSuccessDialog.isShowing()) mSuccessDialog.dismiss();
                    mFailDialog.setName(device.getName());
                    lastRemoteTime = System.currentTimeMillis();
                    break;
                case IntentAction.ACTION_SHOW_REMOTE_DIALOG:
                    useRemoteDialog = true;
                    break;
                case IntentAction.ACTION_NOT_SHOW_REMOTE_DIALOG:
                    useRemoteDialog = false;
                    break;
            }
        }
    }

    private boolean canDrawOverlays(){
        return Settings.canDrawOverlays(this);
    }

    public static App getInstance() {
        return instance;
    }
}
