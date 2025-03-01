package com.chihihx.launcher.databinding

import android.content.pm.ApplicationInfo
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.gson.Gson
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.clickNoRepeat
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.AppItem
import com.chihihx.launcher.bean.WebItem
import com.chihihx.launcher.utils.AndroidSystem
import com.chihihx.launcher.utils.GlideUtils

object SearchBindingAdapter {

    @BindingAdapter(value = ["item_search_icon"])
    @JvmStatic
    fun itemSearchIcon(ivIcon: ImageView, dto: Any) {
        when (dto) {
            is AppItem -> {
                GlideUtils.bind(
                    ivIcon,
                    if (TextUtils.isEmpty(dto.localIcon)) dto.appIcon else dto.localIcon
                )
            }
            is ApplicationInfo->{
                ivIcon.setImageDrawable(dto.loadIcon(appContext.packageManager))
            }
        }


    }

    @BindingAdapter(value = ["item_search_text"])
    @JvmStatic
    fun itemSearchText(tvName: TextView, dto: Any) {
        when (dto) {
            is AppItem -> {
                tvName.text = dto.appName
            }
            is ApplicationInfo->{
                tvName.text = dto.loadLabel(appContext.packageManager)
            }
        }


    }

    @BindingAdapter(value = ["item_search_focus"])
    @JvmStatic
    fun itemSearchFocus(view: View, dto: Any) {
        view.setOnFocusChangeListener { v, hasFocus ->
            v.isSelected = hasFocus
            val animation = AnimationUtils.loadAnimation(
                v.context, if (hasFocus) R.anim.zoom_in_middle else R.anim.zoom_out_middle
            )
            v.startAnimation(animation)
            animation.fillAfter = true
        }

    }

    @BindingAdapter(value = ["item_search_click"])
    @JvmStatic
    fun itemSearchClick(view: View, dto: Any) {
        view.clickNoRepeat {
            when (dto) {
                is AppItem -> {
                    if (!TextUtils.isEmpty(dto.appDownLink)) AndroidSystem.jumpAppStore(
                        view.context,
                        Gson().toJson(dto),
                        null
                    )
                }
                is ApplicationInfo->{
                    AndroidSystem.openPackageName(
                        view.context,
                        dto.packageName
                    )
                }
            }

        }

    }

    @BindingAdapter(value = ["item_search_webicon"])
    @JvmStatic
    fun itemSearchWebIcon(ivIcon: ImageView, dto: WebItem) {
        GlideUtils.bind( ivIcon, dto.icon)


    }

    @BindingAdapter(value = ["item_search_webfocus"])
    @JvmStatic
    fun itemSearchWebFocus(view: View, dto: WebItem) {
        view.setOnFocusChangeListener { v, hasFocus ->
            val animation = AnimationUtils.loadAnimation(
                v.context, if (hasFocus) R.anim.zoom_in_max else R.anim.zoom_out_max
            )
            v.startAnimation(animation)
            animation.fillAfter = true
        }


    }





}