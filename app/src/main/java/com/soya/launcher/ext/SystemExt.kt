package com.soya.launcher.ext

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.jsonToBean
import com.shudong.lib_base.ext.jsonToString
import com.shudong.lib_base.ext.jsonToTypeBean
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.cache.AppCache
import com.soya.launcher.utils.AndroidSystem

/**
 * 扩展函数，根据包名获取应用的图标。
 * 默认优先获取 `bannerres` 资源，如果没有找到则使用 `icon` 资源。
 *
 * @param packageName 应用的包名
 * @return 应用的图标，如果无法找到图标则返回 null
 */
fun String.getAppIcon(): Drawable? {
    return try {
        val packageManager = appContext.packageManager
        val applicationInfo = packageManager.getApplicationInfo(this, 0)
        val resources = packageManager.getResourcesForApplication(applicationInfo)

        // 尝试获取 bannerres 资源
        val bannerResId = resources.getIdentifier("bannerres", "drawable", this)
        if (bannerResId != 0) {
            resources.getDrawable(bannerResId, null)
        } else {
            // 如果没有找到 bannerres 资源，则获取默认的 icon 资源
            packageManager.getApplicationIcon(applicationInfo)
        }
    } catch (e: PackageManager.NameNotFoundException) {
        // 如果包名无法找到，返回 null
        null
    }
}



/**
 * 扩展函数，通过包名获取应用的版本名称（versionName）。
 *
 * @param packageName 应用的包名
 * @return 应用的版本名称，如果无法获取版本名称则返回 null
 */
fun String.getAppVersionName(): String? {
    return try {
        val packageManager = appContext.packageManager
        val packageInfo = packageManager.getPackageInfo(this, 0)
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        // 如果包名无法找到，返回 null
        null
    }
}


fun getUpdateList(): Boolean {
    val updateApps = AppCache.updateInfo.jsonToTypeBean<MutableList<UpdateAppsDTO>>()
    val localApps = AndroidSystem.getUserApps2(appContext).toMutableList()

    // 创建包名到版本号的映射
    val versionCodeMap = mutableMapOf<String, Int>()
    val installedPackageNames = localApps.map { it.packageName }.toSet()

    localApps.forEach { app ->
        try {
            val packageInfo = appContext.packageManager.getPackageInfo(app.packageName, 0)
            versionCodeMap[app.packageName] = packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            // 记录异常信息
            Log.e("UpdateList", "Package not found: ${app.packageName}", e)
        }
    }

    // 筛选需要更新的应用，移除版本号相同的和本地不存在的 UpdateAppsDTO
    val filteredUpdateApps = updateApps.filterNot { dto ->
        val currentVersionCode = versionCodeMap[dto.packageName]
        val isNotInstalled = dto.packageName !in installedPackageNames
        val shouldFilter = currentVersionCode == dto.versionCode || isNotInstalled

        if (shouldFilter) {
            // 日志记录
            Log.d("UpdateList", "Filtering out ${dto.packageName}. " +
                    "Installed: ${!isNotInstalled}, " +
                    "Current Code: $currentVersionCode, DTO Code: ${dto.versionCode}")
        }

        shouldFilter
    }.toMutableList()

    // 更新缓存
    AppCache.updateInfo = filteredUpdateApps.jsonToString()
    return filteredUpdateApps.isNotEmpty()
}



