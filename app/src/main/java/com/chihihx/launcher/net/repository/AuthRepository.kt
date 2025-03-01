package com.chihihx.launcher.net.repository

import com.shudong.lib_base.base.viewmodel.BaseRepository
import com.chihihx.launcher.bean.AuthParamsDto
import com.chihihx.launcher.net.api.AuthApi
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