package com.soya.launcher.ext

import android.content.Intent
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.view.FlikerProgressBar
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.resume

private val installMutex = Mutex()
suspend fun String.silentInstallWithMutex(pbUpdate: FlikerProgressBar?=null): Boolean {
    val context = pbUpdate?.context
    context?.let { pbUpdate.setProgressText(it.getString(R.string.The_author_of_The_New_York_Times)) }
    return installMutex.withLock {
        context?.let { pbUpdate.setProgressText(it.getString(R.string.The_author)) }
        silentInstall()
    }
}

suspend fun String.silentInstall(): Boolean {
    return suspendCancellableCoroutine { continuation ->
        val packageManager = appContext.packageManager
        val packageInstaller = packageManager.packageInstaller
        val apkFile = File(this)

        // 获取 APK 的包名和版本号
        val packageInfo = packageManager.getPackageArchiveInfo(this, 0)
        val packageName = packageInfo?.packageName
            ?: return@suspendCancellableCoroutine continuation.resume(false)

        // 获取 APK 的版本号
        val apkVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }

        // 检查当前安装的版本号
        var installedVersionCode: Long
        try {
            val installedPackageInfo = packageManager.getPackageInfo(packageName, 0)
            installedVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                installedPackageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                installedPackageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            installedVersionCode = -1
        }

        if (installedVersionCode >= apkVersionCode) {
            // 如果本地版本号更高，先卸载应用
            packageName.uninstallApp{isUninstallSuccess->
                isUninstallSuccess.yes {
                    performInstallation(packageInstaller, apkFile, continuation)
                }
            }

        } else {
            // 直接进行安装
            performInstallation(packageInstaller, apkFile, continuation)
        }
    }
}

// 扩展函数用于卸载应用，接受一个 callback 参数
fun String.uninstallApp(callback: (Boolean) -> Unit) {
    try {
        val packageName = this
        val packageManager = appContext.packageManager
        val packageInstaller = packageManager.packageInstaller
        val params =
            PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL).apply {
                setAppPackageName(packageName)
            }
        val sessionId = packageInstaller.createSession(params)

        // 创建卸载的 Intent
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse("package:$packageName")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            sessionId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val sender = pendingIntent.intentSender

        // 执行卸载
        packageInstaller.uninstall(packageName, sender)

        // 使用协程进行轮询检查包是否被卸载
        (currentActivity as? LifecycleOwner)?.lifecycleScope?.launch {
            val pollingInterval = 1000L // 轮询间隔时间，单位为毫秒
            val maxRetries = 5 // 最大重试次数
            repeat(maxRetries) {
                delay(pollingInterval)
                try {
                    appContext.packageManager.getPackageInfo(packageName, 0)
                    // 应用还在
                } catch (e: Exception) {
                    // 应用已经卸载
                    callback(true)
                    return@launch
                }
            }
            callback(false) // 如果超过最大重试次数仍然没卸载成功，返回 false
        }
    } catch (e: Exception) {

        callback(false)
    }
}


private fun performInstallation(
    packageInstaller: PackageInstaller,
    apkFile: File,
    continuation: CancellableContinuation<Boolean>
) {

    val sessionId =
        packageInstaller.createSession(PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL))
    val session = packageInstaller.openSession(sessionId)
    val out = session.openWrite("app_install", 0, -1)
    val buffer = ByteArray(65536)
    var c: Int
    val inputStream = FileInputStream(apkFile)
    try {
        while (inputStream.read(buffer).also { c = it } != -1) {
            out.write(buffer, 0, c)
        }
        session.fsync(out)
    } finally {
        out.close()
        inputStream.close()
    }

    val installCallback = object : PackageInstaller.SessionCallback() {
        override fun onCreated(sessionId: Int) {}

        override fun onFinished(sessionId: Int, success: Boolean) {
            if (continuation.isActive) {

                if (success) {
                    continuation.resume(true)
                } else {
                    continuation.resume(false) // 安装失败
                }
            }
        }

        override fun onActiveChanged(sessionId: Int, active: Boolean) {}
        override fun onBadgingChanged(sessionId: Int) {}
        override fun onProgressChanged(sessionId: Int, progress: Float) {}
    }

    val handler = Handler(Looper.getMainLooper())
    packageInstaller.registerSessionCallback(installCallback, handler)

    val intent = Intent("INSTALL_ACTION").apply {
        // 设置 intent 的 Action 和其他数据
    }
    val pendingIntent =
        PendingIntent.getBroadcast(appContext, sessionId, intent, PendingIntent.FLAG_IMMUTABLE)
    session.commit(pendingIntent.intentSender)

    continuation.invokeOnCancellation {
        session.close()
    }
}


fun File.installApkForNormalApp() {

    val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // 适配 Android 7.0 及以上版本，使用 FileProvider 提供文件 URI
        FileProvider.getUriForFile(appContext, "${appContext.packageName}.provider", this)
    } else {
        Uri.fromFile(this)
    }

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(apkUri, "application/vnd.android.package-archive")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
    }

    try {
        appContext.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e("InstallError", "No activity found to handle APK install", e)
    }


}


fun String.uninstallApkForNormalApp() {
    val packageUri: Uri = Uri.parse("package:$this")
    val intent = Intent(Intent.ACTION_DELETE).apply {
        data = packageUri
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
        appContext.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e("UninstallError", "No activity found to handle APK uninstall", e)
    }
}
