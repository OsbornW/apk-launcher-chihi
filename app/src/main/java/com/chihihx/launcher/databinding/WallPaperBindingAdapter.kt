package com.chihihx.launcher.databinding

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.Wallpaper
import com.chihihx.launcher.manager.PreferencesManager

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