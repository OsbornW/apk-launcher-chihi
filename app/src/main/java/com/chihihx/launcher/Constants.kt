package com.chihihx.launcher

import android.graphics.drawable.Drawable
import com.chihihx.launcher.cache.AppCache.WALLPAPERS
import com.chihihx.launcher.config.Config
import com.chihihx.launcher.manager.PreferencesManager

const val FLAVOUR_H6 = "flavour_h6"
const val PACKAGE_NAME_FILE_MANAGER_713 = "com.softwinner.TvdFileManager"
const val CLASS_NAME_AUTHFRAGMENT = "com.chihihx.launcher.AuthFragment"
const val PACKAGE_NAME_SCREENZOOM_RK3326 = "com.lei.hxkeystone/.ScaleActivity"
const val PACKAGE_NAME_AUTO_RESPONSE = "com.chihihx.launcher/.activity.AutoResponseActivity"
const val PACKAGE_NAME_PROJECTOR_MODE_P50 = "com.android.tv.settings/.display.ProjectionActivity"
const val PACKAGE_NAME_SCREEN_ZOOM_P50 = "com.softwinner.tcorrection/.ScaleActivity"
const val PACKAGE_NAME_KEYSTONE_CORRECTION_P50 = "com.android.tv.settings/.display.AutoCorrectionActivity"
const val PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION = "com.softwinner.tcorrection/.CheckGsensorActivity"
const val PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION_RK3326 = "com.lei.hxkeystone/.CheckGsensorActivity"
const val PACKAGE_NAME_Manual_KEYSTONE_CORRECTION_RK3326 = "com.lei.hxkeystone/.FourPoint"
const val PACKAGE_NAME_IMAGE_MODE = "com.android.tv.settings/.display.PQModeActivity"

// X98K_M系统时间页面
const val PACKAGE_NAME_X98KM_TIME = "com.android.tv.settings/.system.DateTimeActivity"
// X98K_M系统语言页面
const val PACKAGE_NAME_X98KM_LANGUAGE = "com.android.tv.settings/.system.LanguageActivity"
// X98K_M系统声音页面
const val PACKAGE_NAME_X98KM_SOUND = "com.android.tv.settings/.device.displaysound.DisplaySoundActivity"
// X98K_M系统文件管理器页面
const val PACKAGE_NAME_X98KM_FILEMANAGER = "com.droidlogic.FileBrower/.FileBrower"

// 713 Box分辨率设置页面
const val PACKAGE_NAME_713_BOX_DISPLAY = "com.android.tv.settings/.device.display.DisplayActivity"
const val PACKAGE_NAME_713_BOX_BLUETOOTH = "com.android.tv.settings/.bluetooth.BluetoothActivity"
const val PACKAGE_NAME_713_BOX_MOUSE = "com.android.tv.settings/.device.input.MouseModeActivity"
const val PACKAGE_NAME_713_BOX_SOUND = "com.android.tv.settings/.device.sound.SoundActivity"
const val PACKAGE_NAME_713_BOX_INPUT_METHOD = "com.android.tv.settings/.inputmethod.KeyboardActivity"


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
const val SETTING_DISPLAY = 10



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

