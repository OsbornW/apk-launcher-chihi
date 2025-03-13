package com.chihihx.launcher.product.base

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.chihihx.launcher.BuildConfig
import com.chihihx.launcher.bean.DateItem
import com.chihihx.launcher.bean.SettingItem
import com.chihihx.launcher.bean.TypeItem
import com.chihihx.launcher.product.Projector713_M_G
import com.chihihx.launcher.product.Projector713_M_G_2X
import com.chihihx.launcher.product.Projector713_M_G_Test
import com.chihihx.launcher.product.Projector713_M_G_X10
import com.chihihx.launcher.product.ProjectorP50
import com.chihihx.launcher.product.ProjectorRK3326
import com.chihihx.launcher.product.ProjectorRK3326G
import com.chihihx.launcher.product.ProjectorX50
import com.chihihx.launcher.product.TVBoxX98KM
import com.chihihx.launcher.product.TVBox_713

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
    fun isShowPageTitle() = true
    fun openSound(){}
    fun isBlueDisableClick() = false
    fun isJumpGuidGradient() = true
    fun addMoreItem():MutableList<SettingItem>?=null
    fun isShowDefaultVideoApp():Boolean = true
    fun isShowMemoryInfo() = false
    fun addTimeSetItem(
        isAutoTime: Boolean,
        date: String,
        time: String,
        is24: Boolean,
        isShowAllItem: Boolean
    ):MutableList<DateItem>?=null

}

data object DefaultDevice: TVDevice


val channelMap = mapOf(
    BuildConfig.FLAVOR_NAME_LAUNCHER_713M_G to Projector713_M_G(),
    BuildConfig.FLAVOR_NAME_LAUNCHER_713_TEST to Projector713_M_G_Test,
    BuildConfig.FLAVOR_NAME_LAUNCHER_713RK_M_G to ProjectorRK3326G,
    BuildConfig.FLAVOR_NAME_LAUNCHER_713RK_M to ProjectorRK3326,
    BuildConfig.FLAVOR_NAME_LAUNCHER_X98K_M to TVBoxX98KM,
    BuildConfig.FLAVOR_NAME_LAUNCHER_P50 to ProjectorP50(),
    BuildConfig.FLAVOR_NAME_LAUNCHER_713_SMARTTV to TVBox_713,
    BuildConfig.FLAVOR_NAME_LAUNCHER_713M_G_2X to Projector713_M_G_2X,
    BuildConfig.FLAVOR_NAME_LAUNCHER_X50 to ProjectorX50,
    BuildConfig.FLAVOR_NAME_LAUNCHER_X10 to Projector713_M_G_X10
)

val product: TVDevice = channelMap[BuildConfig.FLAVOR_NAME]?:DefaultDevice



