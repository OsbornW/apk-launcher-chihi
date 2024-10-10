package com.soya.launcher.product.base

import android.content.Context
import androidx.fragment.app.Fragment
import com.open.system.ASystemProperties
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION
import com.soya.launcher.R
import com.soya.launcher.SETTING_ABOUT
import com.soya.launcher.SETTING_BLUETOOTH
import com.soya.launcher.SETTING_DATE
import com.soya.launcher.SETTING_LAUNGUAGE
import com.soya.launcher.SETTING_MORE
import com.soya.launcher.SETTING_NETWORK
import com.soya.launcher.SETTING_PROJECTOR
import com.soya.launcher.SETTING_WALLPAPER
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.cache.AppCache
import com.soya.launcher.cache.AppCache.WALLPAPERS
import com.soya.launcher.enums.Types
import com.soya.launcher.ext.openApp
import com.soya.launcher.ext.openFileM
import com.soya.launcher.ui.activity.ChooseGradientActivity
import com.soya.launcher.ui.activity.GradientActivity
import com.soya.launcher.ui.activity.HomeGuideGroupGradientActivity
import com.soya.launcher.ui.activity.LanguageActivity
import com.soya.launcher.ui.activity.ProjectorActivity
import com.soya.launcher.ui.activity.ScaleScreenActivity
import com.soya.launcher.ui.activity.SetDateActivity
import com.soya.launcher.ui.activity.WifiListActivity
import com.soya.launcher.ui.fragment.FocusFragment
import com.soya.launcher.ui.fragment.GuideLanguageFragment
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.ui.fragment.WelcomeFragment
import com.soya.launcher.utils.AndroidSystem

interface TVDeviceImpl : TVDevice {

    // 跳转到时间设置页面
    override fun openDateSetting(context: Context) = context.startKtxActivity<SetDateActivity>()

    // 跳转到语言设置页面
    override fun openLanguageSetting(context: Context) =
        context.startKtxActivity<LanguageActivity>()

    /*
    *
    * 默认是走引导页逻辑
    * */
    override fun switchFragment(): Fragment? = run {
        AppCache.isSkipGuid.no {
            sendLiveEventDataDelay(IS_MAIN_CANBACK, true, 1000)
            AppCache.isGuidChageLanguage.yes {
                GuideLanguageFragment.newInstance()
            }.otherwise {
                FocusFragment.newInstance()
            }

        }.otherwise {
            sendLiveEventDataDelay(IS_MAIN_CANBACK, false, 1000)
            MainFragment.newInstance()
        }
    }

    override fun openFileManager() {
        openFileM()
    }

    override fun openHomeTopKeystoneCorrection(context: Context) {
        context.startKtxActivity<HomeGuideGroupGradientActivity>()
    }

    override fun addProjectorItem(): MutableList<SettingItem>? {
        return mutableListOf<SettingItem>().apply {
            //投影模式/投影方式
            add(
                SettingItem(
                    Projector.TYPE_PROJECTOR_MODE,
                    appContext.getString(R.string.project_mode),
                    R.drawable.baseline_model_training_100
                )
            )
            // 屏幕切边/屏幕缩放
            add(
                SettingItem(
                    Projector.TYPE_SCREEN_ZOOM,
                    appContext.getString(R.string.project_crop),
                    R.drawable.baseline_crop_100
                )
            )
            // 梯形校正
            add(
                SettingItem(
                    Projector.TYPE_KEYSTONE_CORRECTION,
                    appContext.getString(R.string.project_gradient),
                    R.drawable.baseline_screenshot_monitor_100
                )
            )
            // HDMI
            add(
                SettingItem(
                    Projector.TYPE_HDMI,
                    appContext.getString(R.string.project_hdmi),
                    R.drawable.baseline_settings_input_hdmi_100
                )
            )
        }
    }

    override fun initCalibrationText(
        isEnable: Boolean,
        dataList: MutableList<SettingItem?>,
        result: () -> Unit
    ) {
        ASystemProperties.set(
            "persist.vendor.gsensor.enable",
            if (isEnable) "1" else "0"
        )
        dataList[0]?.setName(
            if (isEnable) appContext.getString(R.string.auto) else appContext.getString(
                R.string.close
            )
        )
        dataList[1]?.setName(
            if (isEnable) appContext.getString(R.string.auto_calibration) else appContext.getString(
                R.string.manual
            )
        )
        result.invoke()
    }

    override fun addCalibrationItem(isEnable: Boolean): MutableList<SettingItem>? {
        return mutableListOf<SettingItem>().apply {
            //手自动模式切换
            add(
                SettingItem(
                    Projector.TYPE_KEYSTONE_CORRECTION_MODE,
                    if (isEnable) appContext.getString(R.string.auto) else appContext.getString(R.string.close),
                    R.drawable.auto
                )
            )
            //水平校准和手动校准
            add(
                SettingItem(
                    Projector.TYPE_KEYSTONE_CORRECTION,
                    if (isEnable) appContext.getString(R.string.auto_calibration) else appContext.getString(
                        R.string.manual
                    ),
                    R.drawable.baseline_screenshot_monitor_100
                )
            )
        }
    }

    override fun openScreenZoom() {
        currentActivity?.let {
            it.startKtxActivity<ScaleScreenActivity>()
        }
    }

    override fun openProjectorMode(callback:(isSuccess:Boolean)->Unit) {
        val success = currentActivity?.let { AndroidSystem.openProjectorMode(it) }
        if (success != null) {
            callback.invoke(success)
        }
    }

    override fun openKeystoneCorrectionOptions() {
        currentActivity?.let {
            it.startKtxActivity<ChooseGradientActivity>()
        }
    }

    override fun openManualAutoKeystoneCorrection() {
        val isEnalbe =
            ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1
        isEnalbe.yes {
            PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION.openApp()
        }.otherwise {
            currentActivity?.let { it.startKtxActivity<GradientActivity>() }
        }
    }

    override fun addSettingItem(): MutableList<SettingItem>? {
        return mutableListOf<SettingItem>().apply {
            add(SettingItem(SETTING_NETWORK, appContext.getString(R.string.network), R.drawable.baseline_wifi_100))
            add(SettingItem(
                SETTING_BLUETOOTH,
                appContext.getString(R.string.bluetooth),
                R.drawable.baseline_bluetooth_100
            ))
            add(SettingItem(
                SETTING_PROJECTOR,
                appContext.getString(R.string.pojector),
                R.drawable.baseline_cast_connected_100
            ))
            add(SettingItem(
                SETTING_WALLPAPER,
                appContext.getString(R.string.wallpaper),
                R.drawable.baseline_wallpaper_100
            ))
            add(SettingItem(SETTING_LAUNGUAGE, appContext.getString(R.string.language), R.drawable.baseline_translate_100))
            add(SettingItem(
                SETTING_DATE,
                appContext.getString(R.string.date),
                R.drawable.baseline_calendar_month_100
            ))
            add(SettingItem(SETTING_ABOUT, appContext.getString(R.string.about), R.drawable.baseline_help_100))
            add(SettingItem(SETTING_MORE, appContext.getString(R.string.more), R.drawable.baseline_more_horiz_100))
        }
    }

    override fun openWifi() {
        currentActivity?.let { it.startKtxActivity<WifiListActivity>() }
    }

    override fun openProjector() {
        currentActivity?.let { it.startKtxActivity<ProjectorActivity>() }
    }

    override fun openBluetooth() {
        currentActivity?.let { AndroidSystem.openBluetoothSetting4(it) }
    }

    override fun openMore() {
        currentActivity?.let { AndroidSystem.openSystemSetting(it) }
    }

    override fun projectorColumns(): Int  = 4

    override fun addWallPaper() {
        if (WALLPAPERS.isEmpty()) {
            WALLPAPERS.add(Wallpaper(3, R.drawable.wallpaper_22))
            WALLPAPERS.add(Wallpaper(0, R.drawable.wallpaper_1))
            WALLPAPERS.add(Wallpaper(1, R.drawable.wallpaper_20))
            WALLPAPERS.add(Wallpaper(2, R.drawable.wallpaper_21))
            WALLPAPERS.add(Wallpaper(4, R.drawable.wallpaper_24))
            WALLPAPERS.add(Wallpaper(5, R.drawable.wallpaper_25))
        }
    }

    override fun addHeaderItem(): MutableList<TypeItem>? {
        return mutableListOf<TypeItem>().apply {

            // APP Store
            add(TypeItem(
                appContext.getString(R.string.app_store),
                R.drawable.store,
                0,
                Types.TYPE_APP_STORE,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            ))

            // Local APP
            add(TypeItem(
                appContext.getString(R.string.apps),
                R.drawable.app_list,
                0,
                Types.TYPE_MY_APPS,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            ))


        }
    }

    override fun isGameRes(): Int?  = null
}