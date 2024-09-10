package com.soya.launcher.net.api

import android.database.Observable
import com.shudong.lib_base.base.viewmodel.BaseApi
import com.soya.launcher.bean.AuthParamsDto
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.bean.UpdateDto
import com.soya.launcher.net.CHECK_ACTIVE_CODE
import com.soya.launcher.net.CHECK_VERSION
import com.soya.launcher.net.HOME_INFO
import com.soya.launcher.net.Update_INFO
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap


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

    @GET(Update_INFO)
    suspend fun reqUpdateInfo(
        @Query("req_id") reqId: String,
        @Query("channel") channel: String,
    ): MutableList<UpdateAppsDTO>

    @GET(CHECK_VERSION)
    suspend fun reqVersionInfo(
        @QueryMap map: MutableMap<String, Any>,
    ): UpdateDto


    @POST("xxxxx")
    fun post(@Body body: RequestBody?): Observable<String?>?


}

interface HomeLocalApi : BaseApi {

    @GET(Update_INFO)
    suspend fun reqUpdateInfo(
        @Query("req_id") reqId: String,
        @Query("channel") channel: String,
    ): MutableList<UpdateAppsDTO>


}

interface AuthApi : BaseApi {

    @POST(CHECK_ACTIVE_CODE)
    suspend fun reqCheckActiveCode(
        @Body authParamsDto: AuthParamsDto
    ): String


}