package com.soya.launcher.ext

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.global.AppCacheBase.drawableCache
import com.soya.launcher.GlideApp
import com.soya.launcher.R

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


// 定义函数类型参数用于错误处理
fun ImageView.loadImageWithGlide(
    load: Any,
    errorCallback: (GlideException?) -> Unit
) {
    Glide.with(appContext)
        .load(load)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        //.placeholder(R.drawable.rectangle_solffffff)
        //.error(R.drawable.rectangle_solffffff)
        .transition(DrawableTransitionOptions.with(
            DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()
        ))
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                // 调用回调处理错误
                errorCallback(e)
                // 可以显示自定义的错误图或者进行其他处理
                return true // 返回 false 以让 Glide 继续处理错误图
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                // 处理加载成功的情况
                return false // 返回 false 以让 Glide 继续处理资源
            }


        })
        .into(this)
}
