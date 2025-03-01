package com.chihihx.launcher.ui.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.widget.Toast
import com.shudong.lib_base.ext.e

class AppStateChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val packageName = intent.data?.schemeSpecificPart

        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION == action) {
            // 获取当前的连接状态 (SupplicantState)
            val supplicantState = intent.getParcelableExtra<android.net.wifi.SupplicantState>(WifiManager.EXTRA_NEW_STATE)
            if (supplicantState == android.net.wifi.SupplicantState.COMPLETED) {
                // Wi-Fi 连接成功
                "WiFi连接成功".e("chihi_error")
                Toast.makeText(context, "Wi-Fi 连接成功", Toast.LENGTH_SHORT).show()
            } else if (supplicantState == android.net.wifi.SupplicantState.DISCONNECTED) {
                // Wi-Fi 连接失败
                "WiFi连接失败".e("chihi_error")
                Toast.makeText(context, "Wi-Fi 连接失败", Toast.LENGTH_SHORT).show()
            }else if (supplicantState == android.net.wifi.SupplicantState.ASSOCIATING) {
                // Wi-Fi 连接失败
                "WiFi连接中".e("chihi_error")
                Toast.makeText(context, "Wi-Fi 连接中", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
