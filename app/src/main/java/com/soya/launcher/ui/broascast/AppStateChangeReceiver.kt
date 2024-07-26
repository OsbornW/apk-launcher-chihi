package com.soya.launcher.ui.broascast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import com.shudong.lib_base.ext.sendLiveEventData

class AppStateChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val packageName = intent.data!!.schemeSpecificPart

        if (Intent.ACTION_PACKAGE_CHANGED == action) {
            val pm = context.packageManager
            val state = pm.getApplicationEnabledSetting(packageName)
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                // App disabled
                sendLiveEventData("isenable",false)
                Toast.makeText(context, "$packageName has been disabled", Toast.LENGTH_SHORT).show()
            } else if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                // App enabled
                sendLiveEventData("isenable",true)
                Toast.makeText(context, "$packageName has been enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
