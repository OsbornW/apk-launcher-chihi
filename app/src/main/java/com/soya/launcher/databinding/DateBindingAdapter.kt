package com.soya.launcher.databinding

import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.soya.launcher.bean.AboutItem
import com.soya.launcher.bean.DateItem
import com.soya.launcher.bean.KeyBoardDto
import com.soya.launcher.bean.Language
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.SimpleTimeZone

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