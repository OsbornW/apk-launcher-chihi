package com.soya.launcher;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.open.system.SystemUtils;
import com.soya.launcher.bean.AppItem;
import com.soya.launcher.bean.CacheWeather;
import com.soya.launcher.bean.Movice;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

public class App extends Application {
    //修改渠道信息，修改build.gradle
    //0虹信 1爱泊优投影仪 2爱泊优TV //3 TV-X98K
    public static final boolean IS_TEST = false;
    public static final int COMPANY = 3;
    public static final String USER_ID = "62";

    //宏信
    /*public static final String CHANNEL = "LA27001";
    public static final String CHIHI_TYPE = "H700";
    public static final String MODEL = "cupid";*/

    //爱泊优 投影仪
    /*public static final String CHANNEL = "LA22001";
    public static final String CHIHI_TYPE = "PROJECTOR_001";
    public static final String MODEL = "001";*/

    //爱泊优 TV
    /*public static final String CHANNEL = "LA22001";
    public static final String CHIHI_TYPE = "TV_001";
    public static final String MODEL = "001";*/

    //X98K
    public static final String CHANNEL = "LA23001";
    public static final String CHIHI_TYPE = "X98K";
    public static final String MODEL = "001";

    public static final String APPID = "launcher";
    public static final Map<Integer, List<Movice>> MOVIE_MAP = new ConcurrentHashMap<>();
    public static final CacheWeather WEATHER = new CacheWeather();
    public static final List<Wallpaper> WALLPAPERS = new ArrayList<>();
    public static final List<AppItem> APP_STORE_ITEMS = new CopyOnWriteArrayList<>();
    public static boolean useRemoteDialog = true;

    private InnerReceiver receiver;
    private RemoteDialog mFailDialog;
    private RemoteDialog mSuccessDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        HttpRequest.init(this);
        PreferencesUtils.init(this);

        if (TextUtils.isEmpty(PreferencesUtils.getString(Atts.UID, ""))){
            PreferencesUtils.setProperty(Atts.UID, UUID.randomUUID().toString());
        }
        BluetoothScannerUtils.init(this);
        initRemote();

        WALLPAPERS.add(new Wallpaper(0, R.drawable.wallpaper_1));
        WALLPAPERS.add(new Wallpaper(1, R.drawable.wallpaper_2));
        WALLPAPERS.add(new Wallpaper(2, R.drawable.wallpaper_3));
        WALLPAPERS.add(new Wallpaper(3, R.drawable.wallpaper_15));
        WALLPAPERS.add(new Wallpaper(4, R.drawable.wallpaper_13));
        WALLPAPERS.add(new Wallpaper(5, R.drawable.wallpaper_12));

        com.hs.App.init(this);
        startService(new Intent(this, AppService.class));

        AndroidSystem.setEnableBluetooth(this, true);
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
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) break;
                    if (!mFailDialog.isShowing() && canDrawOverlays() && useRemoteDialog) mFailDialog.show();
                    if (mSuccessDialog.isShowing()) mSuccessDialog.dismiss();
                    mFailDialog.setName(device.getName());
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
}
