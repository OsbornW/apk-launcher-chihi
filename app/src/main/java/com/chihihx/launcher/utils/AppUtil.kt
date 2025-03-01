package com.chihihx.launcher.utils

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.provider.Settings
import androidx.core.content.FileProvider
import com.chihihx.launcher.BuildConfig
import java.io.File

object AppUtil {
    fun installApk(context: Context, filePath: String?) {
        val apkUri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            File(filePath)
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    @Throws(Exception::class)
    fun adbInstallApk(filePath: String): Int {
        val cmd = "pm install -r $filePath"
        val process = Runtime.getRuntime().exec(cmd)
        val code = process.waitFor()
        process.destroy()
        return code
    }

    @Throws(Exception::class)
    fun adbUninstallApk(packageName: String) {
        val cmd = "pm uninstall $packageName"
        val process = Runtime.getRuntime().exec(cmd)
        process.waitFor()
        process.destroy()
    }

    @Throws(Exception::class)
    fun openHDMI() {
        val cmd = "am broadcast -a android.cvim.action.HDMI.SOURCE "
        val process = Runtime.getRuntime().exec(cmd)
        process.waitFor()
        process.destroy()
    }

    @Throws(Exception::class)
    fun restart() {
        val process = Runtime.getRuntime().exec("reboot")
        process.waitFor()
    }

    fun setAutoDate(context: Context, isAuto: Boolean) {
        try {
            Settings.Global.putInt(
                context.contentResolver,
                Settings.Global.AUTO_TIME,
                if (isAuto) 1 else 0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun set24Display(context: Context, is24Display: Boolean): Boolean {
        return Settings.System.putString(
            context.contentResolver,
            Settings.System.TIME_12_24,
            if (is24Display) "24" else "12"
        )
    }

    fun setTimeZone(context: Context, zone: String?): Boolean {
        return Settings.System.putString(
            context.contentResolver,
            Settings.Global.AUTO_TIME_ZONE,
            zone
        )
    }

    fun is24Display(context: Context): Boolean {
        return "24" == Settings.System.getString(
            context.contentResolver,
            Settings.System.TIME_12_24
        )
    }

    fun setTime(timeMillis: Long) {
        SystemClock.setCurrentTimeMillis(timeMillis)
    }
}
