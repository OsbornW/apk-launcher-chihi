package com.soya.launcher.net.api

import com.soya.launcher.net.Update_INFO
import com.shudong.lib_base.base.viewmodel.BaseApi
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.net.HOME_INFO
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/3 11:36
 * @PACKAGE_NAME:  com.thumbsupec.ispruz.home
 */
interface HomeApi : BaseApi {

    @GET(HOME_INFO)
    suspend fun reqHomeInfo(
        @Query("req_id") reqId: String,
        @Query("channel") channel: String,
    ): HomeInfoDto


}

interface HomeLocalApi : BaseApi {

    @GET(Update_INFO)
    suspend fun reqUpdateInfo(
        @Query("req_id") reqId: String,
        @Query("channel") channel: String,
    ): MutableList<UpdateAppsDTO>


}