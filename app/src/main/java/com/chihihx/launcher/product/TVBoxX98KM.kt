package com.chihihx.launcher.product

import android.content.Context
import com.chihihx.launcher.PACKAGE_NAME_X98KM_LANGUAGE
import com.chihihx.launcher.PACKAGE_NAME_X98KM_SOUND
import com.chihihx.launcher.PACKAGE_NAME_X98KM_TIME
import com.chihihx.launcher.ext.openApp

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