package com.soya.launcher.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.IntentAction;
import com.soya.launcher.utils.AppUtils;

public class AppService extends Service {

    private InnerReceiver receiver;
    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentAction.ACTION_DOWNLOAD_APK);
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

    public static class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
        }
    }
}
