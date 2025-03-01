package com.chihihx.launcher.product

import android.content.Context
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.startKtxActivity
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.TypeItem
import com.chihihx.launcher.bean.Wallpaper
import com.chihihx.launcher.cache.AppCache.WALLPAPERS
import com.chihihx.launcher.enums.Types
import com.chihihx.launcher.ext.openBluetoothSettings
import com.chihihx.launcher.product.base.TVDeviceImpl
import com.chihihx.launcher.ui.activity.GradientActivity
import com.chihihx.launcher.ui.activity.InstallModeActivity
import com.chihihx.launcher.utils.AndroidSystem

object ProjectorH6: TVDeviceImpl {
    override fun openHomeTopKeystoneCorrection(context: Context) {
        context.startKtxActivity<GradientActivity>()
    }

    override fun openProjectorMode(callback: (isSuccess: Boolean) -> Unit) {
        currentActivity?.let {
            it.startKtxActivity<InstallModeActivity>()
        }
    }

    override fun openKeystoneCorrectionOptions() {
        currentActivity?.let {
            it.startKtxActivity<GradientActivity>()
        }
    }


    override fun openBluetooth() {
        currentActivity?.openBluetoothSettings()
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
        return mutableListOf<TypeItem>().apply {

            // APP Store
            add(TypeItem(
                appContext.getString(R.string.app_store),
                R.drawable.store,
                0,
                Types.TYPE_APP_STORE,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            ))

            // Local APP
            add(TypeItem(
                appContext.getString(R.string.apps),
                R.drawable.app_list,
                0,
                Types.TYPE_MY_APPS,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            )
            )
            // 投影仪
            add(
                TypeItem(
                appContext.getString(R.string.pojector),
                R.drawable.projector,
                0,
                Types.TYPE_PROJECTOR,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            )
            )
        }
    }

}