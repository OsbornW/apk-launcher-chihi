package com.soya.launcher.product

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.soya.launcher.PACKAGE_NAME_X98KM_LANGUAGE
import com.soya.launcher.PACKAGE_NAME_X98KM_SOUND
import com.soya.launcher.PACKAGE_NAME_X98KM_TIME
import com.soya.launcher.R
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.cache.AppCache.WALLPAPERS
import com.soya.launcher.ext.openApp
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.utils.AndroidSystem

data object TVBoxX98KM : TVBoxX98K(){
    /**
     *
     * 跳转系统时间设置页面
     */
    override fun openDateSetting(context: Context) =
        PACKAGE_NAME_X98KM_TIME.openApp {  }

    /**
     *
     * 跳转语言设置页面
     */
    override fun openLanguageSetting(context: Context) =
        PACKAGE_NAME_X98KM_LANGUAGE.openApp {  }

    override fun openSound() {
        PACKAGE_NAME_X98KM_SOUND.openApp {  }
    }
}