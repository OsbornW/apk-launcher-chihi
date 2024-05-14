package com.shudong.lib_base.base.viewmodel

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/2 17:50
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.base
 */
open class BaseRepository(val baseApi: BaseApi) {


    /*fun reqVerificationCode(params: VerifyCodeParams): Flow<String> = flow {
        emit(baseApi.reqVerificationCode(params))
    }

    fun reqCheckCode(params: VerifyCodeParams): Flow<String> = flow {
        emit(baseApi.reqCheckCode(params))
    }

    *//**
     * 获取用户信息接口
     *//*
    fun reqUserInfo(): Flow<UserInfoDto> = flow {
        emit(baseApi.reqUserInfo(paramsToMap()))
    }

    fun reqShareWeekReport(reportId:String): Flow<ShareWeekReportDto> = flow {
        emit(baseApi.reqShareWeekReport(paramsToMap(
            "reportId",reportId
        )))
    }

    fun reqSyncBrushData(params: SyncBrushDataParams): Flow<MutableList<SyncBrushDataDto>> = flow {
        emit(baseApi.reqSyncBrushData(params))
    }

    fun reqModifyUserInfo(map:MutableMap<String,Any>): Flow<String> = flow {
        emit(baseApi.reqModifyUserInfo(map))
    }*/

}