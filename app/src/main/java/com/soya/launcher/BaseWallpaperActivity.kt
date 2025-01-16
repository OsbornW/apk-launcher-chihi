package com.soya.launcher

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.databinding.ActivityBaseWebBinding
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.ad.AdSdk
import com.soya.launcher.ext.loadBlurDrawable
import com.soya.launcher.ext.setBlurBackground
import com.soya.launcher.utils.GlideUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class BaseWallpaperActivity<VDB : ViewDataBinding, VM : BaseViewModel> :
    BaseVMActivity<VDB, VM>() {

    /**
     * 是否显示壁纸
     */
    open fun isShowWallPaper(): Boolean = true


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

    /*override fun onResume() {
        super.onResume()
        updateWallPaper()
    }*/

    override fun onStart() {
        super.onStart()
        updateWallPaper()
    }


    var job:Job?=null
    var lastResId = 0
    fun updateWallPaper() {
        if(lastResId!= curWallpaper()){
            job?.cancel()
            job = lifecycleScope.launch {
                val resId = curWallpaper()
                val drawable = resId.loadBlurDrawable()
                localWallPaperDrawable = drawable

                rootBinding.layoutRoot.background?.let {
                    rootBinding.layoutRoot.background = localWallPaperDrawable
                }
                if(rootBinding.layoutRoot.background==null){
                    rootBinding.layoutRoot.background = localWallPaperDrawable
                }
                lastResId = resId
            }
        }


    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 判断是否是遥控器的右键
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            // 这里可以执行你需要的逻辑
            val isRemove = AdSdk.removeAd()
            isRemove.yes {
                return true
            }.otherwise {
                return super.onKeyUp(keyCode, event)
            }
        }
        return super.onKeyDown(keyCode, event)
    }




}