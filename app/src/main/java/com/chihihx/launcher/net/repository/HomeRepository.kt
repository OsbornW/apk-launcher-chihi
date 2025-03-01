package com.chihihx.launcher.net.repository

import com.shudong.lib_base.base.viewmodel.BaseRepository
import com.chihihx.launcher.BuildConfig
import com.chihihx.launcher.bean.HomeInfoDto
import com.chihihx.launcher.ad.bean.PluginInfoEntity
import com.chihihx.launcher.bean.UpdateAppsDTO
import com.chihihx.launcher.bean.UpdateDto
import com.chihihx.launcher.net.api.HomeApi
import com.chihihx.launcher.product.base.product
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