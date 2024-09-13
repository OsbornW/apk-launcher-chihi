package com.soya.launcher

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.databinding.ActivityBaseWebBinding
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.yes
import com.soya.launcher.ext.setBlurBackground
import com.soya.launcher.utils.GlideUtils

open class BaseWallpaperActivity<VDB : ViewDataBinding, VM : BaseViewModel> :
    BaseVMActivity<VDB, VM>() {

    /**
     * 是否显示壁纸
     */
    open fun isShowWallPaper(): Boolean = true

    override fun initBeforeContent() {
        /*if(localWallPaperDrawable!=null){
            "我要开始设置啦。。。。".e("zengyue3")
            window.setBackgroundDrawable(localWallPaperDrawable)
        }*/
    }

    override fun initBeforeBaseLayout() {
        // 设置初始透明背景，防止加载时显示默认背景
        isShowWallPaper().yes {
            //GlideUtils.bindBlur(appContext, rootBinding.ivWallpaper, curWallpaper())
            /*rootBinding.layoutRoot.setBlurBackground(curWallpaper()) {
                loadLayout()
            }*/
            rootBinding.layoutRoot.background = localWallPaperDrawable
            loadLayout()
        }
    }


    fun updateWallPaper() {
        val resId = curWallpaper()
        rootBinding.layoutRoot.background?.constantState?.hashCode()?.let {
            if (it != resId.hashCode()) {
                rootBinding.layoutRoot.setBlurBackground(resId)
            }
        }


    }


}