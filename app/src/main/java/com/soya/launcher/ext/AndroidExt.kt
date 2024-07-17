package com.soya.launcher.ext

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log

fun isAndroidAtMost5_1(): Boolean {
    return Build.VERSION.SDK_INT <= 22
}

fun isAndroidVersionAtMost(version:Int): Boolean {
    return Build.VERSION.SDK_INT <= version
}

fun Context.openBluetoothSettings() {
    // 尝试显式启动蓝牙设置活动
    val intent = Intent().apply {
        setClassName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings")
    }
    try {
        startActivity(intent)
    } catch (e: Exception) {
        Log.e("BluetoothSettings", "Explicit intent failed", e)
        // 显式启动失败，使用通用方式启动蓝牙设置页面
        val bluetoothIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        try {
            startActivity(bluetoothIntent)
        } catch (ex: Exception) {
            Log.e("BluetoothSettings", "Fallback intent failed", ex)
            // 通知用户无法打开蓝牙设置（根据需求实现）
        }
    }
}