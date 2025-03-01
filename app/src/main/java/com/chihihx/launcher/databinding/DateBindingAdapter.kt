package com.chihihx.launcher.databinding

import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chihihx.launcher.bean.DateItem

object DateBindingAdapter {

    @BindingAdapter(value = ["item_date_switch"])
    @JvmStatic
    fun itemDateSwitch(switch: Switch, dto: DateItem) {
        switch.isChecked = dto.isSwitch
        switch.visibility = if (dto.isUseSwitch) View.VISIBLE else View.GONE

    }

    @BindingAdapter(value = ["item_date_desc"])
    @JvmStatic
    fun itemDateDesc(tvName: TextView, dto: DateItem) {
        tvName.text = dto.description
        tvName.visibility = if (dto.isUseSwitch) View.GONE else View.VISIBLE

    }



}