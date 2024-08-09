package com.soya.launcher.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.d


fun isSysApp(packageName:String): Boolean {
    // 获取包管理器对象
    val packageManager: PackageManager = appContext.packageManager

    try {
        // 通过包名获取应用信息
        val appInfo = packageManager.getApplicationInfo(packageName, 0)

        // 判断应用是否可卸载
        val isUninstallable = appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
        if (isUninstallable) {
            // 应用可卸载,不属于系统级APP
            // 可以进行相关操作
            return false
        } else {
            // 应用不可卸载,属于系统级APP
            // 提示用户无法卸载

            return true
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return false

}