package com.soya.launcher.product

import android.content.Context
import android.content.pm.ApplicationInfo
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.PACKAGE_NAME_FILE_MANAGER_713
import com.soya.launcher.PACKAGE_NAME_PROJECTOR_MODE_P50
import com.soya.launcher.PACKAGE_NAME_SCREEN_ZOOM_P50
import com.soya.launcher.R
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.enums.Types
import com.soya.launcher.ext.autoResponseText
import com.soya.launcher.ext.convertGameJson
import com.soya.launcher.ext.isGame
import com.soya.launcher.ext.openApp
import com.soya.launcher.p50.setFunctionCorrectionAndFocus
import com.soya.launcher.product.base.TVDeviceImpl

object ProjectorX50 : ProjectorP50() {
    override fun isShowDefaultVideoApp() = false

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

}