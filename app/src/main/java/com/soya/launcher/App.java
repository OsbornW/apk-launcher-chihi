package com.soya.launcher;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.bean.CacheWeather;
import com.soya.launcher.bean.Movice;
import com.soya.launcher.bean.MyRunnable;
import com.soya.launcher.bean.PushApp;
import com.soya.launcher.bean.Wallpaper;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.http.HttpRequest;
import com.soya.launcher.service.AppService;
import com.soya.launcher.ui.dialog.RemoteDialog;
import com.soya.launcher.utils.AndroidSystem;
import com.soya.launcher.utils.BluetoothScannerUtils;
import com.soya.launcher.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    private static App instance;
    private final ExecutorService exec = Executors.newCachedThreadPool();
    public static final List<PushApp> PUSH_APPS = new CopyOnWriteArrayList<>();
    public static final Map<Long, List<Movice>> MOVIE_MAP = new ConcurrentHashMap<>();
    public static final CacheWeather WEATHER = new CacheWeather();
    public static final List<Wallpaper> WALLPAPERS = new ArrayList<>();
    public static final List<AppItem> APP_STORE_ITEMS = new CopyOnWriteArrayList<>();
    public static boolean useRemoteDialog = true;

    private InnerReceiver receiver;
    private RemoteDialog mFailDialog;
    private RemoteDialog mSuccessDialog;
    private MyRunnable remoteRunnable;
    private long lastRemoteTime = -1;

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
        startService(new Intent(this, AppService.class));

        AndroidSystem.setEnableBluetooth(this, true);
        timeRemote();
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
