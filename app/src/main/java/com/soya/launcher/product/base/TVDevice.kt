package com.soya.launcher.product.base

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.soya.launcher.BuildConfig
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.product.Projector713_M_G
import com.soya.launcher.product.ProjectorH6
import com.soya.launcher.product.ProjectorRK3326
import com.soya.launcher.product.TVBoxH27002
import com.soya.launcher.product.TVBoxX98K
import com.soya.launcher.product.PrjectorAiboyou
import com.soya.launcher.product.ProjectorP50
import com.soya.launcher.product.TVBox22001
import com.soya.launcher.product.ProjectorRK3326G
import com.soya.launcher.product.TVBoxAiboyou
import com.soya.launcher.product.TVBoxXHSX
import com.soya.launcher.product.base.Channel.AIBOYOUPROJECTOR
import com.soya.launcher.product.base.Channel.AIBOYOUTV
import com.soya.launcher.product.base.Channel.AIBOYOUTV_TV22001
import com.soya.launcher.product.base.Channel.PROJECTOR_713M_C
import com.soya.launcher.product.base.Channel.PROJECTOR_H6_C
import com.soya.launcher.product.base.Channel.PROJECTOR_P50_C
import com.soya.launcher.product.base.Channel.PROJECTOR_RK3326G_C
import com.soya.launcher.product.base.Channel.PROJECTOR_RK3326_C
import com.soya.launcher.product.base.Channel.TVBox_H27002_C
import com.soya.launcher.product.base.Channel.TVBox_X98K_C
import com.soya.launcher.product.base.Channel.TVBox_XHSX_C

sealed interface TVDevice{
    fun openDateSetting(context: Context){}
    fun openLanguageSetting(context: Context){}
    fun filterRepeatApps(list:MutableList<ApplicationInfo>):MutableList<ApplicationInfo>? = null
    fun switchFragment():Fragment? = null
    fun jumpToAuth(activity: FragmentActivity){}
    fun openFileManager(){}
    fun openHomeTopKeystoneCorrection(context:Context){}
    fun addProjectorItem():MutableList<SettingItem>?=null
    fun initCalibrationText(isEnable:Boolean,dataList:MutableList<SettingItem?>,result:()->Unit){}
    fun addCalibrationItem(isEnable:Boolean = false):MutableList<SettingItem>?=null
    fun openScreenZoom(){}
    fun openProjectorMode(callback:(isSuccess:Boolean)->Unit){}
    fun openKeystoneCorrectionOptions(){}
    fun openManualAutoKeystoneCorrection(){}
    fun addSettingItem():MutableList<SettingItem>?=null
    fun openWifi(){}
    fun openProjector(){}
    fun openBluetooth(){}
    fun openMore(){}
    fun projectorColumns():Int = 0
    fun addWallPaper(){}
    fun addHeaderItem():MutableList<TypeItem>?=null
    fun addGameItem():MutableList<TypeItem>?=null
    fun isGameRes():Int?=null
}

data object DefaultDevice: TVDevice

object Channel{
     const val PROJECTOR_713M_C = "hongxin_713_M_G"
     const val PROJECTOR_RK3326_C = "hongxin_713RK"
     const val PROJECTOR_RK3326G_C = "hongxin_713RK_G"
     const val TVBox_H27002_C = "hongxin_H27002"
     const val TVBox_X98K_C = "yxt_X98K"

    const val PROJECTOR_H6_C = "H6"
    const val AIBOYOUTV = "aiboyou_tv"
    const val AIBOYOUPROJECTOR = "aiboyou_projector"

    const val AIBOYOUTV_TV22001 = "aiboyou_launcher"
    const val PROJECTOR_P50_C = "p50_713M"
    const val TVBox_XHSX_C = "XHSX"
}

val product : TVDevice =
    when (BuildConfig.FLAVOR) {
        PROJECTOR_713M_C -> Projector713_M_G
        PROJECTOR_RK3326_C -> ProjectorRK3326
        PROJECTOR_RK3326G_C -> ProjectorRK3326G
        TVBox_H27002_C -> TVBoxH27002
        TVBox_X98K_C -> TVBoxX98K
        PROJECTOR_H6_C-> ProjectorH6
        AIBOYOUTV -> TVBoxAiboyou
        AIBOYOUPROJECTOR -> PrjectorAiboyou
        AIBOYOUTV_TV22001 -> TVBox22001
        PROJECTOR_P50_C -> ProjectorP50
        TVBox_XHSX_C -> TVBoxXHSX
        else -> DefaultDevice
    }
