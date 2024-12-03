package com.soya.launcher.product.base

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.soya.launcher.BuildConfig
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.product.PrjectorAiboyou
import com.soya.launcher.product.Projector713_M_G
import com.soya.launcher.product.Projector713_M_G_Test
import com.soya.launcher.product.ProjectorH6
import com.soya.launcher.product.ProjectorP50
import com.soya.launcher.product.ProjectorRK3326
import com.soya.launcher.product.ProjectorRK3326G
import com.soya.launcher.product.TVBox22001
import com.soya.launcher.product.TVBoxAiboyou
import com.soya.launcher.product.TVBoxH27002
import com.soya.launcher.product.TVBoxX98K
import com.soya.launcher.product.TVBoxX98KM
import com.soya.launcher.product.TVBoxXHSX
import com.soya.launcher.product.TVBox_713

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

}

data object DefaultDevice: TVDevice


val channelMap = mapOf(
    BuildConfig.FLAVOR_NAME_LAUNCHER_713M_G to Projector713_M_G(),
    BuildConfig.FLAVOR_NAME_LAUNCHER_713_TEST to Projector713_M_G_Test,
    BuildConfig.FLAVOR_NAME_LAUNCHER_713RK to ProjectorRK3326,
    BuildConfig.FLAVOR_NAME_LAUNCHER_713RK_G to ProjectorRK3326G,
    BuildConfig.FLAVOR_NAME_LAUNCHER_H27002 to TVBoxH27002,
    BuildConfig.FLAVOR_NAME_LAUNCHER_X98K to TVBoxX98K(),
    BuildConfig.FLAVOR_NAME_LAUNCHER_X98K_M to TVBoxX98KM,
    BuildConfig.FLAVOR_NAME_LAUNCHER_H6 to ProjectorH6,
    BuildConfig.FLAVOR_NAME_LAUNCHER_AIBOYOU_TV to TVBoxAiboyou,
    BuildConfig.FLAVOR_NAME_LAUNCHER_AIBOYOU_PROJECTOR to PrjectorAiboyou,
    BuildConfig.FLAVOR_NAME_LAUNCHER_AIBOYOU_LAUNCHER to TVBox22001,
    BuildConfig.FLAVOR_NAME_LAUNCHER_P50 to ProjectorP50,
    BuildConfig.FLAVOR_NAME_LAUNCHER_XHSX to TVBoxXHSX,
    BuildConfig.FLAVOR_NAME_LAUNCHER_713TVBox to TVBox_713
)

val product: TVDevice = channelMap[BuildConfig.FLAVOR_NAME]?:DefaultDevice



