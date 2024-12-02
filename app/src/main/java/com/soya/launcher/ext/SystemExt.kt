package com.soya.launcher.ext

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.jsonToString
import com.shudong.lib_base.ext.jsonToTypeBean
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.cache.AppCache
import com.soya.launcher.utils.AndroidSystem
import java.io.File

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
    val localApps = AndroidSystem.getAllInstalledApps(appContext).toMutableList()

    // 创建包名到版本号的映射
    val versionCodeMap = mutableMapOf<String, Int>()
    //val installedPackageNames = localApps.map { it.packageName }.toSet()

    localApps.forEach { app ->
        try {
            val packageInfo = appContext.packageManager.getPackageInfo(app.packageName, 0)
            versionCodeMap[app.packageName] = packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            // 记录异常信息

        }
    }

    // 筛选需要更新的应用，移除版本号相同的和本地不存在的 UpdateAppsDTO
    val filteredUpdateApps = updateApps.filterNot { dto ->
        val currentVersionCode = versionCodeMap[dto.packageName]
        //val isNotInstalled = dto.packageName !in installedPackageNames
        //val shouldFilter = currentVersionCode == dto.versionCode || isNotInstalled
        val shouldFilter = currentVersionCode == dto.versionCode

        if (shouldFilter) {
            // 日志记录
            Log.d(
                "UpdateList", "Filtering out ${dto.packageName}. " +
                        "Current Code: $currentVersionCode, DTO Code: ${dto.versionCode}"
            )
        }else{
            Log.d(
                "UpdateList", "Filtering out ${dto.packageName}. " +
                        "Current Code2: $currentVersionCode, DTO Code2: ${dto.versionCode}"
            )
        }

        shouldFilter
    }.toMutableList()

    // 更新缓存
    AppCache.updateInfo = filteredUpdateApps.jsonToString()
    return filteredUpdateApps.isNotEmpty()
}


/**
 * 对 [MutableList] 的 [ApplicationInfo] 按照安装时间进行排序，最新安装的应用在前面。
 */
fun MutableList<ApplicationInfo>.sortByInstallTime() {
    val pm = appContext.packageManager

    this.sortWith { o1, o2 ->
        val installTime1 = getInstallTime(pm, o1)
        val installTime2 = getInstallTime(pm, o2)
        installTime2.compareTo(installTime1) // 最新的应用在前面
    }
}

/**
 * 获取应用的安装时间
 */
private fun getInstallTime(pm: PackageManager, appInfo: ApplicationInfo): Long {
    return try {
        val packageInfo = pm.getPackageInfo(appInfo.packageName, 0)
        packageInfo.firstInstallTime
    } catch (e: PackageManager.NameNotFoundException) {
        // 如果获取失败，返回一个默认值，这里使用 Long.MIN_VALUE
        Long.MIN_VALUE
    }
}

/**
 * 获取指定应用在排序后集合中的索引位置
 * 如果找不到对应的索引，返回0
 */
fun MutableList<ApplicationInfo>.getIndexAfterSorting(targetAppInfo: ApplicationInfo): Int {
    // 先进行排序
    this.sortByInstallTime()

    // 获取目标应用的信息在排序后集合中的索引位置
    return this.indexOfFirst { it.packageName == targetAppInfo.packageName }.takeIf { it >= 0 } ?: 0
}

fun String.openAppInGooglePlay(context:Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$this"))
        intent.setPackage("com.android.vending")
        context.startActivity(intent)
    } catch (e: Exception) {
        // 如果 Google Play Store 不存在或者跳转失败，使用 Web 浏览器打开
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$this"))
        context.startActivity(intent)
    }
}



fun String.openApp(result: ((Boolean) -> Unit)? = null) {
    val packageManager = appContext.packageManager

    // 检查是否为 `packageName/.activityName` 格式
    if (this.contains("/")) {
        val parts = this.split("/")
        if (parts.size == 2) {
            val packageName = parts[0]
            val activityClassName = if (parts[1].startsWith(".")) {
                // 如果 activityName 以 "." 开头，自动补全完整路径
                packageName + parts[1]
            } else {
                parts[1]
            }
            val intent = Intent().setClassName(packageName, activityClassName)
            try {
                currentActivity?.startActivity(intent)
                result?.invoke(true)
            } catch (e: Exception) {
                result?.invoke(false)
            }
            return
        }
    }

    // 否则，尝试直接启动应用
    val intent = packageManager.getLaunchIntentForPackage(this)
    if (intent != null) {
        try {
            currentActivity?.startActivity(intent)
            result?.invoke(true)
        } catch (e: Exception) {
            result?.invoke(false)
        }
    } else {
        result?.invoke(false)
    }
}


fun String.openApp1(result: (Boolean) -> Unit) {
    val packageManager = appContext.packageManager
    val parts = this.split("/")

    if (parts.size == 2) {
        val packageName = parts[0]
        val activityClassName = parts[1]
        val intent = Intent().setClassName(packageName, activityClassName)
        currentActivity?.startActivity(intent)
        result(true)
    } else {
        val packageName = this
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            currentActivity?.startActivity(intent)
            result(true)
        } else {
            // 如果找不到启动 Intent，尝试打开应用商店页面
            try {
                currentActivity?.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
                result(false)
            } catch (e: android.content.ActivityNotFoundException) {
                // 如果没有应用商店应用，打开网页版
                currentActivity?.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
                result(false)
            }
        }
    }
}

fun openFileM() {
    val intent = Intent()
    intent.setClassName("com.droidlogic.FileBrower", "com.droidlogic.FileBrower.FileBrower")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    if (intent.resolveActivity(appContext.packageManager) != null) {
        currentActivity?.startActivity(intent)
    } else {
        Log.e("FileBrower", "FileBrower is not installed on this device.")
    }
}

// 扩展函数判断是否安装了指定包名的应用
fun String.isAppInstalled(): Boolean {
    return try {
        appContext.packageManager.getPackageInfo(this, 0)
        true // 如果找到了应用，返回 true
    } catch (e: PackageManager.NameNotFoundException) {
        false // 没找到应用，返回 false
    }
}

// 扩展函数：获取内存信息（总内存 / 剩余内存）
fun getMemoryInfo(): String {
    val activityManager = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val totalMemory = memoryInfo.totalMem
    val availableMemory = memoryInfo.availMem

    // 将字节数转换为 GB，并保留两位小数
    val totalMemoryGB = totalMemory.toDouble() / (1024 * 1024 * 1024)  // 转换为 GB
    val availableMemoryGB = availableMemory.toDouble() / (1024 * 1024 * 1024)  // 转换为 GB

    // 格式化为 21.2GB/128GB 形式
    return String.format("%.2fGB/%.2fGB", availableMemoryGB, totalMemoryGB)
}



