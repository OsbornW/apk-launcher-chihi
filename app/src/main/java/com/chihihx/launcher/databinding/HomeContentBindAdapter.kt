package com.chihihx.launcher.databinding

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.Data
import com.chihihx.launcher.bean.HomeDataList
import com.chihihx.launcher.bean.Movice
import com.chihihx.launcher.cache.AppCache
import com.chihihx.launcher.databinding.HomeHeaderBindAdapter.startPollingForCache
import com.chihihx.launcher.ext.loadImageWithGlide
import com.chihihx.launcher.h27002.getDrawableByName
import com.chihihx.launcher.utils.FileUtils
import com.chihihx.launcher.utils.GlideUtils
import java.io.File

object HomeContentBindAdapter {

    @BindingAdapter(value = ["item_home_content_focus"])
    @JvmStatic
    fun itemHomeContentFocus(view: View, dto: Data) {
        view.setOnFocusChangeListener { view, hasFocus ->
            view.isSelected = hasFocus
            val animation = AnimationUtils.loadAnimation(
                view.context, if (hasFocus) R.anim.zoom_in_max else R.anim.zoom_out_max
            )
            animation.fillAfter = true
            view.startAnimation(animation)
        }
    }

    @BindingAdapter(value = ["item_home_content_img"])
    @JvmStatic
    fun itemHomeContentImg(ivIcon: ImageView, dto: Data) {
        when (dto.picType) {
            Movice.PIC_ASSETS -> {

                GlideUtils.bind(
                    ivIcon, FileUtils.readAssets(
                        appContext, dto.imageUrl as String
                    )
                )
            }

            Movice.PIC_NETWORD -> {
                var image = dto.imageUrl

                if (!image.toString().contains("http")) {
                    ivIcon.setImageDrawable(appContext.getDrawableByName(image.toString()))
                } else {
                    val cacheFile = AppCache.homeData.dataList.get(image)?.let { File(it) }
                    if (cacheFile?.exists() == true ) {
                        "当前文件存在2：：${cacheFile.absolutePath}".e("zengyue3")
                        // 使用缓存的 Drawable
                        //mIV.loadFileRadius1(cacheFile)
                        ivIcon.loadImageWithGlide(cacheFile) {
                            // 删除本地缓存文件
                            if (cacheFile.exists()) {
                                cacheFile.delete()
                            }

                            val cacheList = AppCache.homeData.dataList
                            // 从 map 中删除对应的条目
                            cacheList.remove(dto.imageUrl)
                            AppCache.homeData = HomeDataList(cacheList)

                            if (!dto.imageName.isNullOrEmpty()) {

                                val drawable = appContext.getDrawableByName(dto.imageName)
                                ivIcon.setImageDrawable(drawable)
                            }
                        }
                    } else {
                        // 轮询直到有缓存 Drawable

                        if (!dto.imageName.isNullOrEmpty()) {

                            val drawable = appContext.getDrawableByName(dto.imageName)
                            ivIcon.setImageDrawable(drawable)
                        }

                        if (image != null) {
                            startPollingForCache(ivIcon, image)
                        }
                    }
                }

            }

            else -> GlideUtils.bind( ivIcon, R.drawable.transparent)
        }
    }

    @BindingAdapter(value = ["item_home_content_name"])
    @JvmStatic
    fun itemHomeContentName(tvName: TextView, dto: Data) {
        when (dto.picType) {
            Movice.PIC_NETWORD -> {
                tvName.text = dto.id
            }
        }
    }

    @BindingAdapter(value = ["item_home_content_loading"])
    @JvmStatic
    fun itemHomeContentLoading(tvLoading: TextView, dto: Data) {
        when (dto.picType) {
            Movice.PIC_NETWORD -> {
                if (dto.id?.isNotEmpty() == false) {
                    tvLoading.visibility = View.GONE
                }
            }
        }
    }

}