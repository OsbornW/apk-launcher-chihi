package com.soya.launcher.net.repository

import com.shudong.lib_base.base.viewmodel.BaseRepository
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
class HomeRepository(private val api: HomeApi): BaseRepository(api) {


    fun reqHomeInfo(type:String): Flow<MutableList<String>> = flow {
        emit(api.reqHomeInfo("youtube",type,"whj1001"))
    }

}