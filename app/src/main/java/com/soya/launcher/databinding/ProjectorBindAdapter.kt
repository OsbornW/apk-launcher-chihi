package com.soya.launcher.databinding

import android.content.pm.ApplicationInfo
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.animScale
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.view.RelativeLayoutHouse

object ProjectorBindAdapter {

    @BindingAdapter(value = ["item_projector_icon"])
    @JvmStatic
    fun itemProjectorIcon(ivIcon: ImageView, dto: SettingItem) {
       ivIcon.setImageResource(dto.ico)
    }

    @BindingAdapter(value = ["item_projector_name"])
    @JvmStatic
    fun itemProjectorName(tvName: TextView, dto: SettingItem) {
        tvName.text = dto.name
    }


}