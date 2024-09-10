package com.soya.launcher.product

import androidx.fragment.app.Fragment
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
import com.soya.launcher.SETTING_PROJECTOR
import com.soya.launcher.SETTING_WALLPAPER
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.utils.AndroidSystem

object PrjectorAiboyou: TVDeviceImpl{
    override fun switchFragment() = run {
        sendLiveEventDataDelay(IS_MAIN_CANBACK, false,1000)
        MainFragment.newInstance()
    }

    override fun addSettingItem(): MutableList<SettingItem> {
        return mutableListOf<SettingItem>().apply {
            add(SettingItem(SETTING_NETWORK, appContext.getString(R.string.network), R.drawable.baseline_wifi_100))

            add(
                SettingItem(
                    SETTING_PROJECTOR,
                    appContext.getString(R.string.pojector),
                    R.drawable.baseline_cast_connected_100
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

            add(
                SettingItem(
                    SETTING_BLUETOOTH,
                    appContext.getString(R.string.bluetooth),
                    R.drawable.baseline_bluetooth_100
                )
            )
            add(SettingItem(SETTING_ABOUT, appContext.getString(R.string.about), R.drawable.baseline_help_100))
            add(SettingItem(SETTING_MORE, appContext.getString(R.string.more), R.drawable.baseline_more_horiz_100))
        }
    }

    override fun openProjector() {
        currentActivity?.let { AndroidSystem.openActivityName(it, "com.qf.keystone.AllActivity") }
    }

    override fun openBluetooth() {
        currentActivity?.let { AndroidSystem.openBluetoothSetting2(it) }
    }

}