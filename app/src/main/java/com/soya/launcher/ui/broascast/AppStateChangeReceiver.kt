package com.soya.launcher.ui.broascast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class AppStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String packageName = intent.getData().getSchemeSpecificPart();

        if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
            PackageManager pm = context.getPackageManager();
            int state = pm.getApplicationEnabledSetting(packageName);
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                // App disabled
                Toast.makeText(context, packageName + " has been disabled", Toast.LENGTH_SHORT).show();
            } else if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                // App enabled
                Toast.makeText(context, packageName + " has been enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
