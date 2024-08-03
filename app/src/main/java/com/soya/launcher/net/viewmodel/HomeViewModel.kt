package com.soya.launcher.net.viewmodel

import androidx.lifecycle.MutableLiveData
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.net.repository.HomeLocalRepository
import com.soya.launcher.net.repository.HomeRepository
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

}