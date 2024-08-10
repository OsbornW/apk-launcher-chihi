package com.soya.launcher.product

import android.content.pm.ApplicationInfo
import com.soya.launcher.product.base.TVDeviceImpl

object TVBoxH27002 : TVDeviceImpl{
    override fun filterRepeatApps(list: MutableList<ApplicationInfo>): MutableList<ApplicationInfo> {
        val excludedPackageNames = setOf(
            "com.android.vending",
            "com.explorer",
            "com.amazon.avod.thirdpartyclient",
            "com.google.android.apps.youtube.creator",
            "com.netflix.mediaclient"
        )

        return list.filterNot { appInfo ->
            excludedPackageNames.contains(appInfo.packageName ?: "")
        }.toMutableList()
    }
}