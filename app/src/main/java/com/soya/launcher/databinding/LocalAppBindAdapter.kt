package com.soya.launcher.databinding

import android.content.pm.ApplicationInfo
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.no
import com.soya.launcher.R
import com.soya.launcher.ui.dialog.AppDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.isSysApp
import com.soya.launcher.view.AppLayout

object LocalAppBindAdapter {

    @BindingAdapter(value = ["item_local_app_pic"])
    @JvmStatic
    fun itemLocalAppPic(ivIcon: ImageView, dto: ApplicationInfo) {
        ivIcon.setImageDrawable(dto.loadIcon(appContext.packageManager))
    }

    @BindingAdapter(value = ["item_local_app_name"])
    @JvmStatic
    fun itemLocalAppName(tvName: TextView, dto: ApplicationInfo) {
        tvName.text = dto.loadLabel(appContext.packageManager)
    }


    @BindingAdapter(value = ["item_local_app_focus"])
    @JvmStatic
    fun itemLocalAppFocus(view: View, dto: ApplicationInfo) {
        view.setOnFocusChangeListener { view, hasFocus ->
            view.isSelected = hasFocus
            val animation = AnimationUtils.loadAnimation(
                view.context, if (hasFocus) R.anim.zoom_in_middle else R.anim.zoom_out_middle
            )
            view.startAnimation(animation)
            animation.fillAfter = true
        }
    }

    @BindingAdapter(value = ["item_local_app_select"])
    @JvmStatic
    fun itemLocalAppSelect(appLayout: AppLayout, dto: ApplicationInfo) {
        appLayout.setCallback {

        }
    }

    @BindingAdapter(value = ["item_local_app_click"])
    @JvmStatic
    fun itemLocalAppClick(view: View, dto: ApplicationInfo) {
        view.clickNoRepeat {
            currentActivity?.let { it1 -> AndroidSystem.openPackageName(it1, dto.packageName) }
        }
    }

    @BindingAdapter(value = ["item_local_app_keylitsener"])
    @JvmStatic
    fun itemLocalAppkeylitsener(appLayout: AppLayout, dto: ApplicationInfo) {
       appLayout.setListener(AppLayout.EventListener { keyCode, event ->
           if (event?.keyCode == KeyEvent.KEYCODE_MENU) {
               isSysApp(dto.packageName).no {

                   val dialog = AppDialog.newInstance(dto)
                   dialog.setCallback(object : AppDialog.Callback {
                       override fun onOpen() {
                           currentActivity?.let {
                               AndroidSystem.openPackageName(
                                   it,
                                   dto.packageName)
                           }
                       }

                   })
                   //dialog.show(currentActivity?.fragmentManager, AppDialog.TAG)
               }
               return@EventListener false
           }
           true
       })
    }


}