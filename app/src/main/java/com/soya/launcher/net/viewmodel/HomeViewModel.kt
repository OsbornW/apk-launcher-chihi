package com.soya.launcher.net.viewmodel

import androidx.lifecycle.MutableLiveData
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.currentActivity
import com.soya.launcher.App
import com.soya.launcher.bean.Data
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.UpdateAppsDTO
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
    private val repositoryLocal: HomeLocalRepository by inject()

    val firmwareModelMacData = MutableLiveData<Pair<String, String>>()


    fun reqUpdateInfo(): Flow<MutableList<UpdateAppsDTO>> = repositoryLocal.reqUpdateInfo()
    fun reqHomeInfo(): Flow<HomeInfoDto> = repository.reqHomeInfo()

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