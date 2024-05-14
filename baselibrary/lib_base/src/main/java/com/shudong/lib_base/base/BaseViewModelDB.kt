package com.shudong.lib_base.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.shudong.lib_base.base.viewmodel.BaseRepositoryDB
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
open class BaseViewModelDB:ViewModel(),KoinComponent,LifecycleObserver{

    private val repository: BaseRepositoryDB by inject()


    /**
     *
     * 从本地获取所有设备
     */
    /*suspend fun getDeviceFromLocal(mac:String = globalMac) = repository.getDeviceFromLocal(mac)*/



}