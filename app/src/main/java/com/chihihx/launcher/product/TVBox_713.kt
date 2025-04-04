package com.chihihx.launcher.product

import androidx.fragment.app.Fragment
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.PACKAGE_NAME_FILE_MANAGER_713
import com.chihihx.launcher.R
import com.chihihx.launcher.SETTING_ABOUT
import com.chihihx.launcher.SETTING_BLUETOOTH
import com.chihihx.launcher.SETTING_DATE
import com.chihihx.launcher.SETTING_DISPLAY
import com.chihihx.launcher.SETTING_LAUNGUAGE
import com.chihihx.launcher.SETTING_MORE
import com.chihihx.launcher.SETTING_NETWORK
import com.chihihx.launcher.SETTING_WALLPAPER
import com.chihihx.launcher.bean.SettingItem
import com.chihihx.launcher.bean.TypeItem
import com.chihihx.launcher.cache.AppCache
import com.chihihx.launcher.enums.Types
import com.chihihx.launcher.ext.convertGameJson
import com.chihihx.launcher.ext.isGame
import com.chihihx.launcher.ext.openApp
import com.chihihx.launcher.product.base.TVDeviceImpl
import com.chihihx.launcher.ui.activity.MoreActivity
import com.chihihx.launcher.ui.fragment.GuideLanguageFragment
import com.chihihx.launcher.ui.fragment.MainFragment

object TVBox_713 : TVDeviceImpl {
    override fun openFileManager() = PACKAGE_NAME_FILE_MANAGER_713.openApp()

    override fun addHeaderItem(): MutableList<TypeItem> {
        return mutableListOf<TypeItem>().apply {

            // APP Store
            /*add(
                TypeItem(
                    appContext.getString(R.string.app_store),
                    R.drawable.store,
                    0,
                    Types.TYPE_APP_STORE,
                    TypeItem.TYPE_ICON_IMAGE_RES,
                    TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                )
            )*/

            // Local APP
            add(
                TypeItem(
                    appContext.getString(R.string.apps),
                    R.drawable.app_list,
                    0,
                    Types.TYPE_MY_APPS,
                    TypeItem.TYPE_ICON_IMAGE_RES,
                    TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                )
            )

        }
    }

    override fun addGameItem(): MutableList<TypeItem>? = run {
        isGame().yes {
            convertGameJson()
        }.otherwise { null }
    }

    override fun isGameRes(): Int = run {
        isGame().yes {
            1
        }.otherwise { 0 }
    }

    /*
    *
    * 默认是走引导页逻辑
    * */
    override fun switchFragment(): Fragment = run {
        AppCache.isSkipGuid.no {
            sendLiveEventDataDelay(IS_MAIN_CANBACK, true, 1000)
            GuideLanguageFragment.newInstance()
        }.otherwise {
            sendLiveEventDataDelay(IS_MAIN_CANBACK, false, 1000)
            MainFragment.newInstance()
        }
    }

    override fun isJumpGuidGradient() = false

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
                    SETTING_BLUETOOTH,
                    appContext.getString(R.string.bluetooth),
                    R.drawable.baseline_bluetooth_100
                )
            )
            add(
                SettingItem(
                    SETTING_DISPLAY,
                    appContext.getString(R.string.display),
                    R.drawable.icon_display
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
                    SETTING_LAUNGUAGE,
                    appContext.getString(R.string.language),
                    R.drawable.baseline_translate_100
                )
            )
            add(
                SettingItem(
                    SETTING_DATE,
                    appContext.getString(R.string.date),
                    R.drawable.baseline_calendar_month_100
                )
            )
            add(
                SettingItem(
                    SETTING_ABOUT,
                    appContext.getString(R.string.about),
                    R.drawable.baseline_help_100
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
        currentActivity?.let { it.startKtxActivity<MoreActivity>() }
    }

    override fun isShowMemoryInfo() = true

    override fun isShowDefaultVideoApp() = false


}