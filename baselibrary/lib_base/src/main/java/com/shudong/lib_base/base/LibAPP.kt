package com.shudong.lib_base.base

import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.multidex.MultiDexApplication

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/21 09:15
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.base
 */

open class LibAPP: MultiDexApplication(), CameraXConfig.Provider {
    override fun getCameraXConfig() = CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
        .setMinimumLoggingLevel(Log.ERROR).build()
}