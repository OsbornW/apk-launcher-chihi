package com.soya.launcher

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.shudong.lib_base.base.BaseFragment
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.margin
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.yes
import com.soya.launcher.product.base.product
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

    // 是否设置 paddingTop，默认 true
    open val shouldSetPaddingTop: Boolean = true

    fun updateWallpaper() {
        val act = currentActivity as BaseWallpaperActivity<*, *>?
        act?.updateWallPaper()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isShowTitle = product.isShowPageTitle()
        val rootView = view
        val titleLayout = view.findViewById<View>(R.id.layout) // 获取包含的布局
        if (titleLayout!=null){
            shouldSetPaddingTop.yes { isShowTitle.no { rootView.margin(topMargin = com.shudong.lib_dimen.R.dimen.qb_px_12.dimenValue()) } }

            titleLayout.isVisible = isShowTitle
        }

    }
}