package com.soya.launcher.databinding

import android.content.pm.ApplicationInfo
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.margin
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.bean.HomeDataList
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.cache.AppCache
import com.soya.launcher.ext.loadImageWithGlide
import com.soya.launcher.h27002.getDrawableByName
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.FileUtils
import com.soya.launcher.utils.GlideUtils
import java.io.File

object HomeHeaderBindAdapter {

    @BindingAdapter(value = ["item_home_header_focus"])
    @JvmStatic
    fun itemHomeHeaderFocus(view: View, dto: TypeItem) {
        view.setOnFocusChangeListener { view, hasFocus ->
            (dto.itemPosition == 0).yes {
                hasFocus.yes {
                    //view.margin(leftMargin = com.shudong.lib_dimen.R.dimen.qb_px_15.dimenValue())
                }.otherwise {
                    //view.margin(leftMargin = com.shudong.lib_dimen.R.dimen.qb_px_0.dimenValue())

                }
            }
            view.isSelected = hasFocus
            val animation = AnimationUtils.loadAnimation(
                view.context, if (hasFocus) R.anim.zoom_in_max else R.anim.zoom_out_max
            )
            animation.fillAfter = true
            view.startAnimation(animation)
        }
    }


    @BindingAdapter(value = ["item_home_header_click"])
    @JvmStatic
    fun itemHomeHeaderClick(view: View, dto: TypeItem) {
        view.clickNoRepeat {

        }
    }

    @BindingAdapter(value = ["item_home_header_img"])
    @JvmStatic
    fun itemHomeHeaderImg(ivIcon: ImageView, dto: TypeItem) {
        when (dto.iconType) {
            TypeItem.TYPE_ICON_IMAGE_RES -> ivIcon.setImageResource((dto.icon as Int))
            TypeItem.TYPE_ICON_ASSETS -> GlideUtils.bind(
                ivIcon, FileUtils.readAssets(
                    appContext, dto.icon as String
                )
            )

            else ->      {

                if(!dto.icon.toString().contains("http")){
                    ivIcon.setImageDrawable(appContext.getDrawableByName(dto.icon.toString()))
                }else{

                    val cacheFile = AppCache.homeData.dataList.get(dto.icon)?.let { File(it) }
                    if (cacheFile?.exists()==true&&AppCache.isAllDownload) {
                        // 使用缓存的 Drawable
                        ivIcon.loadImageWithGlide(cacheFile){
                            // 删除本地缓存文件
                            if (cacheFile.exists()) {
                                cacheFile.delete()
                            }

                            val cacheList = AppCache.homeData.dataList
                            // 从 map 中删除对应的条目
                            cacheList.remove(dto.icon)
                            AppCache.homeData = HomeDataList(cacheList)

                            if( !dto.iconName.isNullOrEmpty()){
                                ivIcon.setImageDrawable(appContext.getDrawableByName(dto.iconName.toString()))
                            }
                        }
                    } else {
                        // 轮询直到有缓存 Drawable

                        if(!dto.iconName.isNullOrEmpty()){
                            ivIcon.setImageDrawable(appContext.getDrawableByName(dto.iconName.toString()))
                        }
                        startPollingForCache(ivIcon,dto.icon.toString())
                    }
                }


            }


        }
    }

    fun startPollingForCache( mIV:ImageView, image: String) {
        val handler = Handler(Looper.getMainLooper())
        val pollingInterval = 500L // 轮询间隔（毫秒）
        val maxAttempts = 20 // 最大轮询次数
        var attempts = 0

        val runnable = object : Runnable {
            override fun run() {
                val cacheFile = AppCache.homeData.dataList.get(image)?.let { File(it) }
                if (cacheFile?.exists()==true&& AppCache.isAllDownload) {
                    // 使用缓存的 Drawable

                    GlideUtils.bind( mIV, cacheFile)
                } else if (attempts < maxAttempts) {
                    attempts++
                    handler.postDelayed(this, pollingInterval)
                } else {

                    // 处理没有缓存的情况，可能需要使用默认图像或错误处理
                }
            }
        }
        handler.post(runnable)
    }

}