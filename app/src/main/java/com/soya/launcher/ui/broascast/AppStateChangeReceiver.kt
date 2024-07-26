package com.soya.launcher.ui.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.shudong.lib_base.ext.sendLiveEventData

class AppStateChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val packageName = intent.data?.schemeSpecificPart

        Log.d("TAG", "进入此方法 $action 进入了方法 $packageName")

        if (Intent.ACTION_PACKAGE_CHANGED == action && packageName != null) {
            val pm = context.packageManager
            val state = pm.getApplicationEnabledSetting(packageName)

            when (state) {PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER, PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED -> {
                    println("进入禁用方法")
                    // App disabled
                    sendLiveEventData("isenable",false)
                    Toast.makeText(context, "$packageName 已被禁用", Toast.LENGTH_SHORT).show()
                }
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> {

                    println("进入启用方法")
                    // App enabled
                    sendLiveEventData("isenable",true)
                    Toast.makeText(context, "$packageName 已被启用", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    println("进入其他的")
                }
            }
        }
    }
}
