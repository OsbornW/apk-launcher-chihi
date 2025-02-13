package com.soya.launcher.desktop

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R

fun Activity.setAppInfo(
    appInfo: ApplicationInfo,
    ivAppIcon: ImageView,
    tvAppName: TextView,
    rlSmall: RelativeLayout,
    ivSmall: ImageView
) {
    val pm = packageManager

    appInfo.packageName.isNullOrEmpty().yes {
        //ivAppIcon.setImageResource(R.drawable.icon_add_more)
        rlSmall.isVisible = false
        ivAppIcon.isVisible = true
       // ivAppIcon.loadGlideRadius(R.drawable.icon_add_more)
        //tvAppName.text = R.string.app_text_49.stringValue()
    }.otherwise {
        tvAppName.text = pm.getApplicationLabel(appInfo)

        val resDrawable = appInfo.packageName.getAppIconPair()
        if (resDrawable == null) {
            rlSmall.isVisible = false
            ivAppIcon.isVisible = true
            //ivAppIcon.setImageResource(R.drawable.app_icon_your_company)
            ivAppIcon.loadGlideRadius(R.drawable.app_icon_your_company)
        } else {
            if (resDrawable.second) {
                //是Banner图片
                rlSmall.isVisible = false
                ivAppIcon.isVisible = true
                //ivAppIcon.setImageDrawable(resDrawable)
                ivAppIcon.loadGlideRadius(resDrawable.first)
            } else {
                rlSmall.isVisible = true
                ivAppIcon.isVisible = false
                //ivAppIcon.setImageDrawable(resDrawable)
                ivSmall.loadGlideRadius(resDrawable.first)
            }
        }
    }
}

fun String.getAppIconPair(): Pair<Drawable, Boolean>? {
    return try {
        val packageManager = appContext.packageManager
        val applicationInfo = packageManager.getApplicationInfo(this, 0)
        val resources = packageManager.getResourcesForApplication(applicationInfo)

        // 尝试获取 bannerres 资源
        val bannerResId = applicationInfo.banner
        if (bannerResId != 0) {
            Pair(applicationInfo.loadBanner(packageManager), true)
            //resources.getDrawable(bannerResId, null)
        } else {
            // 如果没有找到 bannerres 资源，则获取默认的 icon 资源
            Pair(packageManager.getApplicationIcon(applicationInfo), false)
        }
    } catch (e: PackageManager.NameNotFoundException) {
        // 如果包名无法找到，返回 null
        null
    }
}

fun ImageView.loadGlideRadius(
    any: Any,
    radius: Int = com.shudong.lib_dimen.R.dimen.qb_px_5.dimenValue()
) {
    Glide.with(appContext)
        .load(any)
        //.placeholder(defaultPic)  // 设置占位符
        //.error(defaultPic)        // 设置错误图片
        .transform(
            jp.wasabeef.glide.transformations.RoundedCornersTransformation(
                radius,
                0
            )
        ) // 设置圆角半径
        .into(this)
}