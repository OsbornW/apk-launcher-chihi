package com.soya.launcher.ext

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.shudong.lib_base.ext.appContext
import com.soya.launcher.GlideApp

fun ImageView.bindImageView(path:Any,placeHolderRes:Drawable?){
    GlideApp.with(appContext)
        .load(path)
        .placeholder(placeHolderRes) // 设置占位符
        .error(placeHolderRes) // 设置错误图片
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .transition(
            DrawableTransitionOptions.with(
                DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()
            )
        )
        .into(this)
}