package com.soya.launcher.fragment

import androidx.lifecycle.MutableLiveData
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.bean.AuthBean
import com.soya.launcher.bean.AuthParamsDto
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.net.repository.AuthRepository
import com.soya.launcher.net.repository.HomeRepository
import com.soya.launcher.utils.toTrim
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
class AuthViewModel : BaseViewModel() {

    private val repository: AuthRepository by inject()
    //private val repositoryLocal: HomeLocalRepository by inject()

    val firmwareModelMacData = MutableLiveData<Pair<String, String>>()


    fun reqCheckActiveCode(activeCode: String): Flow<String> = repository.reqCheckActiveCode(
        AuthParamsDto(activeCode.replace('-', ' ').toTrim())
    )


}