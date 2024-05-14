package com.shudong.lib_base.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thumbsupec.lib_base.ext.*
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/19 17:18
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.base
 */
open class BaseViewModel:ViewModel(),KoinComponent,LifecycleObserver{

    //private val repository: BaseRepository by inject()

    var userNameDta = MutableLiveData<String>()
    var codeData = MutableLiveData<String>()








    /**
     * 获取用户信息
     */
   // fun reqUserInfo(): Flow<UserInfoDto> = repository.reqUserInfo()



    /**
     *
     * 分享周报
     */
   // fun reqShareWeekReport(reportId:String): Flow<ShareWeekReportDto> = repository.reqShareWeekReport(reportId)




    /*open fun reqModifyUserInfo(): Flow<String> = repository.reqModifyUserInfo(
        paramsToMap()
    )*/


}