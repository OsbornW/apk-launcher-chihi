package com.soya.launcher.net.viewmodel

import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.DeviceUtils
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.currentActivity
import com.soya.launcher.App
import com.soya.launcher.BuildConfig
import com.soya.launcher.bean.Data
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.bean.UpdateDto
import com.soya.launcher.net.repository.HomeLocalRepository
import com.soya.launcher.net.repository.HomeRepository
import com.soya.launcher.utils.AndroidSystem
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/2 16:43
 * @PACKAGE_NAME:  com.thumbsupec.ispruz.home.viewmodel
 */
class HomeViewModel : BaseViewModel() {

    private val repository: HomeRepository by inject()
    //private val repositoryLocal: HomeLocalRepository by inject()

    val firmwareModelMacData = MutableLiveData<Pair<String, String>>()


    fun reqUpdateInfo(): Flow<MutableList<UpdateAppsDTO>> = repository.reqUpdateInfo()
    fun reqHomeInfo(): Flow<HomeInfoDto> = repository.reqHomeInfo()

    fun reqLauncherVersionInfo(): Flow<UpdateDto> = repository.reqVersionInfo(
        hashMapOf(
            "appId" to BuildConfig.APP_ID,
            "channel" to BuildConfig.CHANNEL,
            "chihi_type" to BuildConfig.CHIHI_TYPE,
            "version" to BuildConfig.VERSION_CODE.toString(),
            "sdk" to Build.VERSION.SDK_INT.toString(),
            "uuid" to DeviceUtils.getUniqueDeviceId(),
            "model" to BuildConfig.MODEL,
            "brand" to Build.BRAND,
            "product" to Build.PRODUCT,
        )
    )


    fun handleContentClick(bean: Data,successInvoke:(isSuccess:Boolean)->Unit) {
        val skip = bean.packageNames?.any { appPackage ->
            App.SKIP_PAKS.contains(appPackage.packageName)
        } ?: false


        val success = if (skip) {
            currentActivity?.let { AndroidSystem.jumpVideoApp(it, bean.packageNames, null) }
        } else {
            currentActivity?.let { AndroidSystem.jumpVideoApp(it, bean.packageNames, bean.url) }
        }?:false
        successInvoke.invoke(success)
        //if (!success) {
            //toastInstallPKApp(bean.appName ?: "", bean.packageNames)
        //}
    }

}