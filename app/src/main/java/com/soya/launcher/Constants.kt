package com.soya.launcher

import android.graphics.drawable.Drawable
import com.soya.launcher.cache.AppCache.WALLPAPERS
import com.soya.launcher.config.Config
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.utils.GlideUtils

const val FLAVOUR_H6 = "flavour_h6"
const val PACKAGE_NAME_FILE_MANAGER_713 = "com.softwinner.TvdFileManager"
const val CLASS_NAME_AUTHFRAGMENT = "com.soya.launcher.fragment.AuthFragment"
const val PACKAGE_NAME_SCREENZOOM_RK3326 = "com.lei.hxkeystone/com.lei.hxkeystone.ScaleActivity"
const val PACKAGE_NAME_AUTO_RESPONSE = "com.soya.launcher/com.soya.launcher.activity.AutoResponseActivity"
const val PACKAGE_NAME_PROJECTOR_MODE_P50 = "com.android.tv.settings/com.android.tv.settings.display.ProjectionActivity"
const val PACKAGE_NAME_SCREEN_ZOOM_P50 = "com.softwinner.tcorrection/com.softwinner.tcorrection.ScaleActivity"
const val PACKAGE_NAME_KEYSTONE_CORRECTION_P50 = "com.android.tv.settings/com.android.tv.settings.display.AutoCorrectionActivity"
const val PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION = "com.hxdevicetest/com.hxdevicetest.CheckGsensorActivity"
const val PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION_RK3326 = "com.lei.hxkeystone/com.lei.hxkeystone.CheckGsensorActivity"
const val PACKAGE_NAME_Manual_KEYSTONE_CORRECTION_RK3326 = "com.lei.hxkeystone/com.lei.hxkeystone.FourPoint"


const val SETTING_NETWORK = 0
const val SETTING_WALLPAPER = 1
const val SETTING_PROJECTOR = 2
const val SETTING_LAUNGUAGE = 3
const val SETTING_DATE = 4
const val SETTING_BLUETOOTH = 5
const val SETTING_ABOUT = 6
const val SETTING_MORE = 7
const val SETTING_SOUND = 8
const val SETTING_KEYBOARD = 9



const val LAYOUTTYPE_HOME_PORTRAIT = 1
const val LAYOUTTYPE_HOME_LANDSCAPE = 0
const val LAYOUTTYPE_HOME_GAME = 2
//const val LAYOUTTYPE_HOME_APPSTORE = 11
//const val LAYOUTTYPE_HOME_APPLIST = 12
//const val LAYOUTTYPE_HOME_PROJECTOR = 13


var localWallPaperDrawable:Drawable?=null
fun defaultId() = if (Config.COMPANY == 0) R.drawable.wallpaper_22 else R.drawable.wallpaper_1
fun curWallpaper() = run {
    val wallpaperResId =
        WALLPAPERS.find { it.id == PreferencesManager.getWallpaper() }?.picture ?: defaultId()
    wallpaperResId
}

