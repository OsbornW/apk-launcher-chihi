package com.soya.launcher.net.api

import com.soya.launcher.net.Update_INFO
import com.shudong.lib_base.base.viewmodel.BaseApi
import com.soya.launcher.bean.AuthParamsDto
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.ad.bean.PluginInfoEntity
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.bean.UpdateDto
import com.soya.launcher.net.CHECK_ACTIVE_CODE
import com.soya.launcher.net.CHECK_VERSION
import com.soya.launcher.net.HOME_INFO
import com.soya.launcher.net.PLUGIN_INFO
import com.soya.launcher.net.SEARCH_APPLIST
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
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
        @Query("launch_type") launchType: Int? = null, // launchType 是可选参数，默认值为 null
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

    @GET(PLUGIN_INFO)
    suspend fun reqPluginInfo(
        @QueryMap map: MutableMap<String, Any>,
    ): PluginInfoEntity

    @FormUrlEncoded
    @POST(SEARCH_APPLIST)
    suspend fun reqSearchAppList(
        @FieldMap map: MutableMap<String, Any>,
        @QueryMap map1: MutableMap<String, Any>
    ): String


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