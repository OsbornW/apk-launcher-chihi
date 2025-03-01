package com.chihihx.launcher.product

import android.content.pm.ApplicationInfo
import androidx.fragment.app.FragmentActivity
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.reflectFragment
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.chihihx.launcher.CLASS_NAME_AUTHFRAGMENT
import com.chihihx.launcher.R
import com.chihihx.launcher.SETTING_MORE
import com.chihihx.launcher.SETTING_NETWORK
import com.chihihx.launcher.SETTING_WALLPAPER
import com.chihihx.launcher.bean.SettingItem
import com.chihihx.launcher.bean.TypeItem
import com.chihihx.launcher.bean.Wallpaper
import com.chihihx.launcher.cache.AppCache.WALLPAPERS
import com.chihihx.launcher.product.base.TVDeviceImpl
import com.chihihx.launcher.ui.fragment.MainFragment
import com.chihihx.launcher.utils.AndroidSystem

object TVBoxH27002 : TVDeviceImpl {
    override fun filterRepeatApps(list: MutableList<ApplicationInfo>): MutableList<ApplicationInfo> {
        val excludedPackageNames = setOf(
            "com.android.vending",
            "com.explorer",
            "com.amazon.avod.thirdpartyclient",
            "com.google.android.apps.youtube.creator",
            "com.netflix.mediaclient",
            "com.yutube.app",
            "com.naifei.app",
            "com.dsn.app",
            "com.amazon.app",
        )

        return list.filterNot { appInfo ->
            excludedPackageNames.contains(appInfo.packageName ?: "")
        }.toMutableList()
    }

    override fun switchFragment() = run {
        AppCacheBase.isActive.yes {
            sendLiveEventDataDelay(IS_MAIN_CANBACK, false, 1000)
            MainFragment.newInstance()
        }.otherwise {
            CLASS_NAME_AUTHFRAGMENT.reflectFragment()
        }
    }

    override fun jumpToAuth(activity: FragmentActivity) = activity.replaceFragment(
        CLASS_NAME_AUTHFRAGMENT.reflectFragment(), R.id.main_browse_fragment
    )


    override fun addSettingItem(): MutableList<SettingItem> {
        return mutableListOf<SettingItem>().apply {
            add(
                SettingItem(
                    SETTING_NETWORK,
                    appContext.getString(R.string.network),
                    R.drawable.baseline_wifi_100
                )
            )

            add(
                SettingItem(
                    SETTING_WALLPAPER,
                    appContext.getString(R.string.wallpaper),
                    R.drawable.baseline_wallpaper_100
                )
            )
            add(
                SettingItem(
                    SETTING_MORE,
                    appContext.getString(R.string.more),
                    R.drawable.baseline_more_horiz_100
                )
            )
        }
    }

    override fun openMore() {
        currentActivity?.let { AndroidSystem.openSystemSetting2(it) }
    }

    override fun addWallPaper() {
        if (WALLPAPERS.isEmpty()) {
            WALLPAPERS.add(Wallpaper(0, R.drawable.wallpaper_1))
            WALLPAPERS.add(Wallpaper(1, R.drawable.wallpaper_20))
            WALLPAPERS.add(Wallpaper(2, R.drawable.wallpaper_21))
            WALLPAPERS.add(Wallpaper(3, R.drawable.wallpaper_22))
            WALLPAPERS.add(Wallpaper(4, R.drawable.wallpaper_24))
            WALLPAPERS.add(Wallpaper(5, R.drawable.wallpaper_25))
        }
    }

    override fun addHeaderItem(): MutableList<TypeItem> {
        return mutableListOf()
    }


}