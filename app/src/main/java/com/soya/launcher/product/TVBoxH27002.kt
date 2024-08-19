package com.soya.launcher.product

import android.content.pm.ApplicationInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.reflectFragment
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.CLASS_NAME_AUTHFRAGMENT
import com.soya.launcher.R
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.fragment.MainFragment

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

    override fun switchFragment() = run{
        AppCacheBase.isActive.yes {
            sendLiveEventDataDelay(IS_MAIN_CANBACK, false, 1000)
            MainFragment.newInstance()
        }.otherwise {
            CLASS_NAME_AUTHFRAGMENT.reflectFragment()
        }
    }

    override fun jumpToAuth(activity: FragmentActivity)  = activity.replaceFragment(
        CLASS_NAME_AUTHFRAGMENT.reflectFragment(), R.id.main_browse_fragment)
}