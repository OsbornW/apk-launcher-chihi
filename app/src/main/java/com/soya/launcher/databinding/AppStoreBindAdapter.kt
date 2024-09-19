package com.soya.launcher.databinding

import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.shudong.lib_base.ext.clickNoRepeat
import com.soya.launcher.R
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.Notify
import com.soya.launcher.h27002.getDrawableByName
import com.soya.launcher.product.base.product
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.view.MyFrameLayoutHouse

object AppStoreBindAdapter {



    /**
     * 首页App Store的Item图片
     */
    @BindingAdapter(value = ["item_store_icon"])
    @JvmStatic
    fun itemStoreIcon(ivIcon: ImageView, dto:AppItem) {
        if (TextUtils.isEmpty(dto.localIcon)) {
            GlideUtils.bind(ivIcon, dto.appIcon)
        } else {
            ivIcon.setImageDrawable(ivIcon.context.getDrawableByName(dto.localIcon))
        }

    }

    /**
     * 首页App Store的Item描述
     */
    @BindingAdapter(value = ["item_store_desc"])
    @JvmStatic
    fun itemStoreDesc(tvDesc: TextView, dto:AppItem) {
        if (!TextUtils.isEmpty(dto.appSize)) tvDesc.text =
            String.format("%.01f★ | %s", dto.score, dto.appSize)
        else tvDesc.text = ""

    }

    /**
     * 首页App Store的焦点监听事件
     */
    @BindingAdapter(value = ["item_store_focus"])
    @JvmStatic
    fun itemStoreFocus(fl: MyFrameLayoutHouse, dto:AppItem) {
        fl.setOnFocusChangeListener { view, hasFocus ->
            fl.isSelected = hasFocus
            val animation = AnimationUtils.loadAnimation(
                fl.context, if (hasFocus) R.anim.zoom_in_middle else R.anim.zoom_out_middle
            )
            fl.startAnimation(animation)
            animation.fillAfter = true
        }
    }


}