package com.soya.launcher.net.repository

import com.shudong.lib_base.base.viewmodel.BaseRepository
import com.soya.launcher.BuildConfig
import com.soya.launcher.bean.AuthBean
import com.soya.launcher.bean.AuthParamsDto
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.net.api.AuthApi
import com.soya.launcher.net.api.HomeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/2 17:52
 * @PACKAGE_NAME:  com.thumbsupec.ispruz.home.repository
 */
class AuthRepository(private val api: AuthApi): BaseRepository(api) {


    fun reqCheckActiveCode(authParamsDto: AuthParamsDto): Flow<String> = flow {
        emit(api.reqCheckActiveCode(authParamsDto))
    }

}