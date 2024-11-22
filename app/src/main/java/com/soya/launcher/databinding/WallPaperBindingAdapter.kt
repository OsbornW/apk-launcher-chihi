package com.soya.launcher.databinding

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.soya.launcher.R
import com.soya.launcher.bean.AboutItem
import com.soya.launcher.bean.KeyBoardDto
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.manager.PreferencesManager

object WallPaperBindingAdapter {

    @BindingAdapter(value = ["item_wallpaper_icon"])
    @JvmStatic
    fun itemWallPaperIcon(ivIcon: ImageView, dto: Wallpaper) {
        ivIcon.setImageResource(dto.picture)

    }

    @BindingAdapter(value = ["item_wallpaper_check"])
    @JvmStatic
    fun itemWallPaperCheck(ivCheck: ImageView, dto: Wallpaper) {
        ivCheck.visibility =
            if (dto.id == PreferencesManager.getWallpaper()) View.VISIBLE else View.GONE

    }


    @BindingAdapter(value = ["item_wallpaper_focus"])
    @JvmStatic
    fun itemWallPaperFocus(view: View, dto: Wallpaper) {
        view.setOnFocusChangeListener { v, hasFocus ->
            v.isSelected = hasFocus
            val animation = AnimationUtils.loadAnimation(
                v.context, if (hasFocus) R.anim.zoom_in_max else R.anim.zoom_out_max
            )
            v.startAnimation(animation)
            animation.fillAfter = true
        }

    }


}