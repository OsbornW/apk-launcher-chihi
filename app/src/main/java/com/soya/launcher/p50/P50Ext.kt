package com.soya.launcher.p50

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shudong.lib_base.ext.appContext

/**
 * 自动响应: persist.vendor.auto_focus.hysd=true (false为关闭)
 *
 * 投影方式: startActivity "com.android.tv.settings", "com.android.tv.settings.display.ProjectionActivity"
 * 屏幕缩放: startActivity "com.softwinner.tcorrection", "com.softwinner.tcorrection.ScaleActivity"
 *
 * 梯形校正: startActivity com.android.tv.settings", "com.android.tv.settings.display.AutoCorrectionActivity
 */
fun setFunction(function: String, callback: ((Boolean) -> Unit?)? =null) {
    if (isServiceRunning(appContext, "com.hysd.hyscreen.VAF2S")) {
        return
    }

    var workmode = 1
    var if_af = 1
    var if_key = 1
    var dmode = 3
    var castmode = -2
    var functest = 0

    //  function：0:标定, 1:全功能, 2:自动对焦, 3:自动入幕&避障
    when (function) {
        CALIBRATION -> {
            workmode = 0
            if_af = 1
            if_key = 0
            dmode = 0
        }

        FULL_FUNCTION -> {
            if_af = 1
            if_key = 1
            dmode = 2
        }

        AUTO_FOCUS -> {
            if_af = 1
            if_key = 0
            dmode = 0
        }

        AUTO_ENTRY -> {
            if_af = 0
            if_key = 1
            dmode = 3
        }
    }

    try {
        val intent = Intent()
        intent.setComponent(ComponentName("com.hysd.hyscreen", "com.hysd.hyscreen.VAF2S"))
        intent.putExtra("mode", workmode)   // 0 标定 1 工作 2 缩放联动
        intent.putExtra("afswitch", if_af)  //对焦开关 1 打开 0 关闭
        intent.putExtra("keyswitch", if_key)    //梯形开关 1 打开，0 关闭
        intent.putExtra("dmode", dmode)    // 0 梯形 1 梯形+幕布 2 梯形+避障 3 梯形+幕布+避障 4 对焦+入幕
        intent.putExtra("castmode", castmode)
        intent.putExtra("test", functest)
        appContext.startService(intent)
        callback?.invoke(true)
    } catch (e: Exception) {
        callback?.invoke(false)
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

const val CALIBRATION = "CALIBRATION"   //标定，校正
const val FULL_FUNCTION = "FULL_FUNCTION"   //全功能
const val AUTO_FOCUS = "AUTO_FOCUS" // 自动对焦
const val AUTO_ENTRY = "AUTO_ENTRY" //自动入幕/避障