package com.soya.launcher.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.soya.launcher.App.Companion.instance
import com.soya.launcher.GlideApp
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.File

object GlideUtils {
    @JvmOverloads
    fun bind(
        context: Context?,
        view: ImageView?,
        load: Any?,
        strategy: DiskCacheStrategy? = DiskCacheStrategy.ALL
    ) {
        GlideApp.with(instance!!)
            .load(load)
            .diskCacheStrategy(strategy!!) //.placeholder(R.drawable.rectangle_solffffff)
            //.error(R.drawable.rectangle_solffffff)
            .transition(
                DrawableTransitionOptions.with(
                    DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()
                )
            )
            .into(view!!)
    }

    fun bind(context: Context?, view: ImageView?, load: Any?, duration: Int) {
        GlideApp.with(instance!!)
            .load(load)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(
                DrawableTransitionOptions.with(
                    DrawableCrossFadeFactory.Builder(duration).setCrossFadeEnabled(true).build()
                )
            )
            .into(view!!)
    }

    fun bindCrop(context: Context?, view: ImageView?, load: Any?) {
        GlideApp.with(instance!!)
            .load(load)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .transition(
                DrawableTransitionOptions.with(
                    DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()
                )
            )
            .into(view!!)
    }

    @JvmOverloads
    fun bindBlur(
        context: Context?,
        view: ImageView?,
        load: Any?,
        placeholder: Drawable? = null,
        radius: Int = 25,
        sampling: Int = 8
    ) {
        GlideApp.with(instance!!)
            .load(load)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placeholder)
            .override(640, 360)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(radius, sampling)))
            .into(view!!)
    }

    fun bindBlurCross(context: Context?, view: ImageView?, load: Any?, duration: Int) {
        GlideApp.with(instance!!)
            .load(load)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 8)))
            .transition(
                DrawableTransitionOptions.with(
                    DrawableCrossFadeFactory.Builder(duration).setCrossFadeEnabled(true).build()
                )
            )
            .into(view!!)
    }

    fun download(context: Context?, url: Any?, listener: DownloadListener?) {
        GlideApp.with(context!!).downloadOnly().load(url).diskCacheStrategy(DiskCacheStrategy.DATA)
            .addListener(object : RequestListener<File?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<File?>,
                    isFirstResource: Boolean
                ): Boolean {
                    listener?.onCallback(null)
                    return false
                }

                override fun onResourceReady(
                    resource: File,
                    model: Any,
                    target: Target<File?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    listener?.onCallback(resource)
                    return false
                }
            }).preload()
    }

    interface DownloadListener {
        fun onCallback(resource: File?)
    }
}