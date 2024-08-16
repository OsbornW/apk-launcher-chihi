package com.soya.launcher.product.base

import android.content.Context
import android.content.pm.ApplicationInfo
import com.soya.launcher.BuildConfig
import com.soya.launcher.product.Projector713G
import com.soya.launcher.product.Projector713M
import com.soya.launcher.product.ProjectorH6
import com.soya.launcher.product.ProjectorRK3326
import com.soya.launcher.product.TVBoxH27002
import com.soya.launcher.product.TVBoxX98K
import com.soya.launcher.product.PrjectorAiboyou
import com.soya.launcher.product.TVBoxAiboyou
import com.soya.launcher.product.base.Channel.AIBOYOUPROJECTOR
import com.soya.launcher.product.base.Channel.AIBOYOUTV
import com.soya.launcher.product.base.Channel.PROJECTOR_713G_C
import com.soya.launcher.product.base.Channel.PROJECTOR_713M_C
import com.soya.launcher.product.base.Channel.PROJECTOR_H6_C
import com.soya.launcher.product.base.Channel.PROJECTOR_RK3326_C
import com.soya.launcher.product.base.Channel.TVBox_H27002_C
import com.soya.launcher.product.base.Channel.TVBox_X98K_C

sealed interface TVDevice{
    fun openDateSetting(context: Context){}
    fun openLanguageSetting(context: Context){}
    fun filterRepeatApps(list:MutableList<ApplicationInfo>):MutableList<ApplicationInfo>? = null
}

data object DefaultDevice: TVDevice

object Channel{
     const val PROJECTOR_713M_C = "hongxin_713M"
     const val PROJECTOR_RK3326_C = "hongxin_713RK"
     const val TVBox_H27002_C = "hongxin_H27002"
     const val TVBox_X98K_C = "yxt_X98K"

    const val PROJECTOR_713G_C = "hongxin_713G"
    const val PROJECTOR_H6_C = "H6"
    const val AIBOYOUTV = "aiboyou_tv"
    const val AIBOYOUPROJECTOR = "aiboyou_projector"
}

val product : TVDevice =
    when (BuildConfig.FLAVOR) {
        PROJECTOR_713M_C -> Projector713M
        PROJECTOR_RK3326_C -> ProjectorRK3326
        TVBox_H27002_C -> TVBoxH27002
        TVBox_X98K_C -> TVBoxX98K
        PROJECTOR_713G_C -> Projector713G
        PROJECTOR_H6_C-> ProjectorH6
        AIBOYOUTV -> TVBoxAiboyou
        AIBOYOUPROJECTOR -> PrjectorAiboyou
        else -> DefaultDevice
    }
