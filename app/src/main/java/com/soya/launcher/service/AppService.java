package com.soya.launcher.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.soya.launcher.R;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.ui.dialog.RemoteDialog;
import com.soya.launcher.utils.AppUtils;

public class AppService extends Service {

    private InnerReceiver receiver;
    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentAction.ACTION_UPGRADE_APP);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case IntentAction.ACTION_UPGRADE_APP:
                    try {
                        String path = intent.getStringExtra(Atts.BEAN);
                        AppUtils.adbInstallApk(path);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
