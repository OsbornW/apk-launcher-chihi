package com.chihihx.launcher.product

import android.content.Context
import android.content.pm.ApplicationInfo
import com.shudong.lib_base.ext.appContext
import com.chihihx.launcher.PACKAGE_NAME_FILE_MANAGER_713
import com.chihihx.launcher.PACKAGE_NAME_PROJECTOR_MODE_P50
import com.chihihx.launcher.PACKAGE_NAME_SCREEN_ZOOM_P50
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.Projector
import com.chihihx.launcher.bean.SettingItem
import com.chihihx.launcher.bean.TypeItem
import com.chihihx.launcher.enums.Types
import com.chihihx.launcher.ext.autoResponseText
import com.chihihx.launcher.ext.openApp
import com.chihihx.launcher.p50.setFunctionCorrectionAndFocus
import com.chihihx.launcher.product.base.TVDeviceImpl

open class ProjectorP50 : TVDeviceImpl {
    override fun openFileManager() = PACKAGE_NAME_FILE_MANAGER_713.openApp()
    override fun openHomeTopKeystoneCorrection(context: Context) {
        setFunctionCorrectionAndFocus()
        //super.openHomeTopKeystoneCorrection(context)
    }

    override fun addProjectorItem(): MutableList<SettingItem> {
        return mutableListOf<SettingItem>().apply {
            //自动响应
            add(
                SettingItem(
                    Projector.TYPE_AUTO_RESPONSE,
                    autoResponseText(),
                    R.drawable.icon_auto_response
                )
            )
            // 自动聚焦
            add(
                SettingItem(
                    Projector.TYPE_AUTO_FOCUS,
                    appContext.getString(R.string.auto_focus),
                    R.drawable.icon_auto_focus
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
            // 自动入幕
            add(
                SettingItem(
                    Projector.TYPE_AUTO_ENTRY,
                    appContext.getString(R.string.auto_entry),
                    R.drawable.icon_auto_entry
                )
            )
            // 投影方式
            add(
                SettingItem(
                    Projector.TYPE_PROJECTOR_MODE,
                    appContext.getString(R.string.projector_method),
                    R.drawable.baseline_model_training_100
                )
            )
            // 屏幕缩放
            add(
                SettingItem(
                    Projector.TYPE_SCREEN_ZOOM,
                    appContext.getString(R.string.screen_zoom),
                    R.drawable.baseline_crop_100
                )
            )

            // 图像模式
            add(
                SettingItem(
                    Projector.TYPE_IMAGE_MODE,
                    appContext.getString(R.string.image_mode),
                    R.drawable.icon_pic_mode
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
        // 不做任何实现
    }

    override fun addCalibrationItem(isEnable: Boolean): MutableList<SettingItem> {
        return mutableListOf<SettingItem>().apply {
            //自动梯形
            add(
                SettingItem(
                    Projector.TYPE_AUTO_CALIBRATION,
                    appContext.getString(R.string.auto_calibrate),
                    R.drawable.icon_auto_at
                )
            )
            // 手动梯形
            add(
                SettingItem(
                    Projector.TYPE_MANUAL_CALIBRATION,
                    appContext.getString(R.string.manual_calibration),
                    R.drawable.icon_auto_mt
                )
            )

        }
    }

    override fun openProjectorMode(callback: (isSuccess: Boolean) -> Unit) =
        PACKAGE_NAME_PROJECTOR_MODE_P50.openApp()

    override fun openScreenZoom() = PACKAGE_NAME_SCREEN_ZOOM_P50.openApp()

    override fun projectorColumns(): Int = 4

    override fun addHeaderItem(): MutableList<TypeItem> {
        return mutableListOf<TypeItem>().apply {

            // APP Store
            /*add(
                TypeItem(
                    appContext.getString(R.string.app_store),
                    R.drawable.store,
                    0,
                    Types.TYPE_APP_STORE,
                    TypeItem.TYPE_ICON_IMAGE_RES,
                    TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                )
            )*/

            // Local APP
            add(
                TypeItem(
                    appContext.getString(R.string.apps),
                    R.drawable.app_list,
                    0,
                    Types.TYPE_MY_APPS,
                    TypeItem.TYPE_ICON_IMAGE_RES,
                    TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                )
            )

            // 投屏功能
            add(
                TypeItem(
                    appContext.getString(R.string.screencast),
                    R.drawable.screencast,
                    0,
                    Types.TYPE_ScreenCast,
                    TypeItem.TYPE_ICON_IMAGE_RES,
                    TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                )
            )
            // 投影仪
            add(
                TypeItem(
                    appContext.getString(R.string.pojector),
                    R.drawable.projector,
                    0,
                    Types.TYPE_PROJECTOR,
                    TypeItem.TYPE_ICON_IMAGE_RES,
                    TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                )
            )
        }
    }


    override fun filterRepeatApps(list: MutableList<ApplicationInfo>): MutableList<ApplicationInfo> {
        val excludedPackageNames = setOf(
            "com.hysd.hyscreen",
        )

        return list.filterNot { appInfo ->
            excludedPackageNames.contains(appInfo.packageName ?: "")
        }.toMutableList()
    }

    override fun isShowPageTitle() = false

}