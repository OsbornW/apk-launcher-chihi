package com.soya.launcher.product

import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.soya.launcher.R
import com.soya.launcher.SETTING_ABOUT
import com.soya.launcher.SETTING_BLUETOOTH
import com.soya.launcher.SETTING_DATE
import com.soya.launcher.SETTING_LAUNGUAGE
import com.soya.launcher.SETTING_MORE
import com.soya.launcher.SETTING_NETWORK
import com.soya.launcher.SETTING_SOUND
import com.soya.launcher.SETTING_WALLPAPER
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.cache.AppCache.WALLPAPERS
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.utils.AndroidSystem

object TVBoxXHSX: TVDeviceImpl{
    override fun switchFragment() = run {
        sendLiveEventDataDelay(IS_MAIN_CANBACK, false, 1000)
        MainFragment.newInstance()
    }

    override fun addSettingItem(): MutableList<SettingItem> {
        return mutableListOf<SettingItem>().apply {
            add(SettingItem(SETTING_NETWORK, appContext.getString(R.string.network), R.drawable.baseline_wifi_100))

            add(
                SettingItem(
                    SETTING_SOUND,
                    appContext.getString(R.string.sound),
                    R.drawable.baseline_settings_voice_100
                )
            )
            add(
                SettingItem(
                    SETTING_WALLPAPER,
                    appContext.getString(R.string.wallpaper),
                    R.drawable.baseline_wallpaper_100
                )
            )
            add(SettingItem(SETTING_LAUNGUAGE, appContext.getString(R.string.language), R.drawable.baseline_translate_100))
            add(
                SettingItem(
                    SETTING_DATE,
                    appContext.getString(R.string.date),
                    R.drawable.baseline_calendar_month_100
                )
            )

            add(SettingItem(SETTING_ABOUT, appContext.getString(R.string.about), R.drawable.baseline_help_100))
            add(SettingItem(SETTING_MORE, appContext.getString(R.string.more), R.drawable.baseline_more_horiz_100))
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

}