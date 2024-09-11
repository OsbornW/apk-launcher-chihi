package com.soya.launcher.utils

import android.net.wifi.ScanResult

object AccessPointState {
    const val WPA2: String = "WPA2"
    const val WPA: String = "WPA"
    const val WEP: String = "WEP"
    const val OPEN: String = "Open"
    const val WPA_EAP: String = "WPA-EAP"
    const val IEEE8021X: String = "IEEE8021X"

    fun getScanResultSecurity(scanResult: ScanResult): String {
        val cap = scanResult.capabilities
        val securityModes = arrayOf(WEP, WPA, WPA2, WPA_EAP, IEEE8021X)
        for (i in securityModes.indices.reversed()) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i]
            }
        }

        return OPEN
    }
}
