package com.soya.launcher.databinding

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.soya.launcher.bean.AboutItem
import com.soya.launcher.bean.KeyBoardDto
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.SimpleTimeZone

object TimeZoneBindingAdapter {

    @BindingAdapter(value = ["item_timezone_name"])
    @JvmStatic
    fun itemTimeZoneName(tvName: TextView, dto: SimpleTimeZone) {
        tvName.text = dto.name

    }

    @BindingAdapter(value = ["item_timezone_desc"])
    @JvmStatic
    fun itemTimeZoneDesc(tvName: TextView, dto: SimpleTimeZone) {
        tvName.text = dto.desc

    }



}