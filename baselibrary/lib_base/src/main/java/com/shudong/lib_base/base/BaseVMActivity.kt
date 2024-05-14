package com.shudong.lib_base.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/19 16:58
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.base
 */
abstract class BaseVMActivity<VDB : ViewDataBinding,VM:BaseViewModel>:BaseActivity<VDB>() {


    val mViewModel: VM by lazy {
        var type = javaClass.genericSuperclass
        var modelClass: Class<VM> = (type as ParameterizedType).actualTypeArguments[1] as Class<VM>
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[modelClass]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycle.addObserver(mViewModel)
        super.onCreate(savedInstanceState)
    }












}