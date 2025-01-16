package com.soya.launcher

import android.content.Context
import android.content.res.Configuration
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
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.margin
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.yes
import com.soya.launcher.product.base.product
import java.lang.reflect.ParameterizedType
import java.util.Locale

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

    open fun excuteLang(){}

    // 标志变量，表示语言是否已经更新
    private var isLanguageUpdated = false

    override fun onStart() {
        super.onStart()

        if(!isLanguageUpdated){
            // 获取系统的默认语言（用户设定的语言）
            val newLocale = Locale.getDefault()

            // 创建一个新的 Configuration 对象
            val config = Configuration(resources.configuration)

            // 设置新的语言
            config.setLocale(newLocale)

            // 更新资源配置
            resources.updateConfiguration(config, resources.displayMetrics)

            // 可能需要手动刷新界面或者重启某些组件来应用语言变更
            excuteLang()
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 获取系统默认语言
        val newLocale = Locale.getDefault()

        // 更新配置
        val config = Configuration(resources.configuration)
        config.setLocale(newLocale)

        // 更新资源配置
        resources.updateConfiguration(config, resources.displayMetrics)
        isLanguageUpdated = true
    }
}