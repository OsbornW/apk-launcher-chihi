package com.soya.launcher.p50

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shudong.lib_base.ext.appContext

fun autoFocus() {
    if (isServiceRunning(appContext, "com.hysd.hyscreen.VAF2S")) {
        return
    }

    Log.d("zengyue", "gsensor vaFocus")
    val workmode = 1
    val if_af = 1
    val if_key = 1
    val dmode = 3
    val castmode = -2
    val functest = 0
    try {
        val intent = Intent()
        intent.setComponent(ComponentName("com.hysd.hyscreen", "com.hysd.hyscreen.VAF2S"))
        intent.putExtra("mode", workmode)
        intent.putExtra("afswitch", if_af)
        intent.putExtra("keyswitch", if_key)
        intent.putExtra("dmode", dmode)
        intent.putExtra("castmode", castmode)
        intent.putExtra("test", functest)

        appContext.startService(intent)
    } catch (e: Exception) {
        Log.w(
            "zengyue",
            "gsensor Launch " + " com.hysd.hyscreen" + " Fail with Message: " + e.message
        )
    }
}

fun isServiceRunning(context: Context, serviceName: String): Boolean {
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in am.getRunningServices(Int.MAX_VALUE)) {
        if (serviceName == service.service.className) {
            return true
        }
    }
    return false
}