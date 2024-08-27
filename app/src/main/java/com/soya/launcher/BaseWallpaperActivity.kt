package com.soya.launcher

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.databinding.ActivityBaseWebBinding
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.yes
import com.soya.launcher.utils.GlideUtils

open class BaseWallpaperActivity<VDB : ViewDataBinding,VM:BaseViewModel> : BaseVMActivity<VDB ,VM>() {

    /**
     * 是否显示壁纸
     */
    open fun isShowWallPaper(): Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isShowWallPaper().yes {
            GlideUtils.bindBlur(appContext, rootBinding.ivWallpaper, curWallpaper())
            //rootBinding.layoutBody.setBackgroundResource(R.drawable.wallpaper_22)
        }
    }

    override fun onResume() {
        super.onResume()
        updateWallPaper()
    }

    fun updateWallPaper(){
        val resId = curWallpaper()

        rootBinding.ivWallpaper.drawable?.constantState?.hashCode()?.let {
            if (it != resId.hashCode()) {
                GlideUtils.bindBlur(appContext, rootBinding.ivWallpaper, resId)
            }
        }


    }


}