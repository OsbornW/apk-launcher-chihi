package com.soya.launcher

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.shudong.lib_base.base.BaseFragment
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.currentActivity
import java.lang.reflect.ParameterizedType

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/19 17:49
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.base
 */
abstract class BaseWallPaperFragment<VDB : ViewDataBinding, VM : BaseViewModel> :
    BaseVMFragment<VDB, VM>() {

    fun updateWallpaper() {
        val act = currentActivity as BaseWallpaperActivity<*, *>?
        act?.updateWallPaper()
    }
}