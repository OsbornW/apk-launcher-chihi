package com.soya.launcher.net.api

import com.soya.launcher.net.Update_INFO
import com.shudong.lib_base.base.viewmodel.BaseApi
import com.soya.launcher.bean.UpdateAppsDTO
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

    @GET(Update_INFO)
    suspend fun reqHomeInfo(
        @Query("app_name") appName: String,
        @Query("type") type: String,
        @Query("channel") channel: String
    ): MutableList<String>


}

interface HomeLocalApi : BaseApi {

    @GET(Update_INFO)
    suspend fun reqUpdateInfo(
        @Query("req_id") reqId: String,
        @Query("channel") channel: String,
    ): MutableList<UpdateAppsDTO>


}