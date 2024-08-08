package com.soya.launcher.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.global.AppCacheBase.drawableCache
import com.soya.launcher.GlideApp

fun ImageView.bindImageView(path:Any){
    GlideApp.with(appContext)
        .load(path)
        //.placeholder(placeHolderRes) // 设置占位符
        //.error(placeHolderRes) // 设置错误图片
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .transition(
            DrawableTransitionOptions.with(
                DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()
            )
        )
        .into(this)
}

fun Any.bindNew(

) {
    GlideApp.with(appContext)
        .load(this)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .transition(
            DrawableTransitionOptions.with(
                DrawableCrossFadeFactory.Builder(800).setCrossFadeEnabled(true).build()
            )
        )
        .into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                // 当资源加载完成时，返回 `Drawable`
                // 在这里处理 `Drawable`
                //this@bindNew.setImageDrawable(resource)
                // val localUrl = AppCache.drawableList
                if (this@bindNew is String) {
                    drawableCache[this@bindNew] = resource
                }


            }

        })
}
