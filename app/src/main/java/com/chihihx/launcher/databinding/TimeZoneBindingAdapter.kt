package com.chihihx.launcher.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chihihx.launcher.bean.SimpleTimeZone

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