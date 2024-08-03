package com.soya.launcher.net.repository

import com.shudong.lib_base.base.viewmodel.BaseRepository
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.net.api.HomeLocalApi
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
class HomeLocalRepository(private val api: HomeLocalApi): BaseRepository(api) {


    fun reqUpdateInfo(): Flow<MutableList<UpdateAppsDTO>> = flow {
        emit(api.reqUpdateInfo("0","LG27001"))
    }

}