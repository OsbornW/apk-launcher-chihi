package com.soya.launcher.ext

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.shudong.lib_base.ext.appContext

/**
 * 判断当前界面是否是桌面
 */
 fun isHome(): Boolean {
    val mActivityManager = appContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val rti = mActivityManager.getRunningTasks(3)
    rti.forEach {
        Log.e("chihi_error", "当前的任务栈=" + it.topActivity!!.className)
    }
    return getHomes().contains(rti[0].topActivity!!.packageName)
}

/**
 * 获得属于桌面的应用的应用包名称
 *
 * @return 返回包含所有包名的字符串列表
 */
private fun getHomes(): List<String> {
    val names: MutableList<String> = ArrayList()
    val packageManager = appContext.packageManager
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    val resolveInfo = packageManager.queryIntentActivities(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )
    for (ri in resolveInfo) {
        names.add(ri.activityInfo.packageName)
        Log.e("vvvvvvvvvvvv", "name=" + ri.activityInfo.packageName)
    }
    return names
}