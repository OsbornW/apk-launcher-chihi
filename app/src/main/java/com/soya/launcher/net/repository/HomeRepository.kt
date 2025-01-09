package com.soya.launcher.net.repository

import com.shudong.lib_base.base.viewmodel.BaseRepository
import com.soya.launcher.BuildConfig
import com.soya.launcher.bean.AuthBean
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.PluginInfoEntity
import com.soya.launcher.bean.SearchDto
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.bean.UpdateDto
import com.soya.launcher.net.api.HomeApi
import com.soya.launcher.product.base.product
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
class HomeRepository(private val api: HomeApi): BaseRepository(api) {

    fun reqHomeInfo(): Flow<HomeInfoDto> = flow {
        emit(api.reqHomeInfo("0", BuildConfig.CHANNEL, product.isGameRes()))
    }

    fun reqUpdateInfo(): Flow<MutableList<UpdateAppsDTO>> = flow {
        emit(api.reqUpdateInfo("0",BuildConfig.CHANNEL))
    }

    fun reqVersionInfo(map: MutableMap<String, Any>): Flow<UpdateDto> = flow {
        emit(api.reqVersionInfo(map))
    }

    fun reqPluginInfo(map: MutableMap<String, Any>): Flow<PluginInfoEntity> = flow {
        emit(api.reqPluginInfo(map))
    }

    fun reqSearchAppList(
        map: MutableMap<String, Any>,
        map1: MutableMap<String, Any>
    ): Flow<String> = flow {
        emit(api.reqSearchAppList(map, map1))
    }


}