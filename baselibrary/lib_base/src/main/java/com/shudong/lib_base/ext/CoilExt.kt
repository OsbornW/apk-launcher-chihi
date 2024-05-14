package com.shudong.lib_base.ext

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.google.android.material.internal.ViewUtils
import com.shudong.lib_base.R
import java.io.File

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/8 15:13
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

private val defaultPic = R.drawable.rectangle_solffffff

/**
 * 加载圆形图片
 */
/*fun ImageView.loadCircle(url: String?) {
    this.load(url) {
        crossfade(true)
        placeholder(R.drawable.icon_default_avatar).error(R.drawable.icon_default_avatar)
        transformations(CircleCropTransformation())
    }
}*/

fun ImageView.loadRadius(url: String?, size: Int = 20) {
    this.load(url) {
        scaleType = ImageView.ScaleType.FIT_XY
        crossfade(true)
        placeholder(defaultPic).error(defaultPic)
        transformations(RoundedCornersTransformation(ViewUtils.dpToPx(appContext, size)))
    }
}

fun ImageView.loadRadiusDrawable(drawable: Drawable, size: Int = 12) {
    this.load(drawable) {
        scaleType = ImageView.ScaleType.CENTER_CROP
        crossfade(true)
        placeholder(defaultPic).error(defaultPic)
        transformations(RoundedCornersTransformation(ViewUtils.dpToPx(appContext, size)))
    }
}



fun ImageView.loadRadius(url: Int, topSize: Int = 20,bottomSize:Int = 20) {
    this.load(url) {
        scaleType = ImageView.ScaleType.FIT_XY
        crossfade(true)
        placeholder(defaultPic).error(defaultPic)
        transformations(RoundedCornersTransformation(
            ViewUtils.dpToPx(appContext, topSize),
            ViewUtils.dpToPx(appContext, topSize),
            ViewUtils.dpToPx(appContext, bottomSize),
            ViewUtils.dpToPx(appContext, bottomSize)
        )
        )
    }
}


fun ImageView.loadFileRadius(path: String?, size: Int = 10) {
    this.load(File(path)) {
        scaleType = ImageView.ScaleType.FIT_XY
        crossfade(true)
        //placeholder(defaultPic).error(defaultPic)
        transformations(RoundedCornersTransformation(ViewUtils.dpToPx(appContext, size)))
    }
}
/*fun ImageView.loadRadius(file:File) {
    this.load(file){
        transformations(RoundedCornersTransformation(
            ViewUtils.dpToPx(appContext, 10),
            ViewUtils.dpToPx(appContext, 10),
            ViewUtils.dpToPx(appContext, 10),
            ViewUtils.dpToPx(appContext, 10)
        )
        )
    }
}*/

/*fun ImageView.loadBanner(url: String?, size: Int = 20) {
    this.load(url) {
        scaleType = ImageView.ScaleType.FIT_XY
        crossfade(true)
        placeholder(R.drawable.shape_radius_holder).error(R.drawable.shape_radius_holder)
        transformations(RoundedCornersTransformation(ViewUtils.dpToPx(appContext, size)))
    }
}*/

fun ImageView.loadPic(url: String?,defaultPic:Int = R.drawable.rectangle_solffffff) {
    this.load(url) {
        crossfade(true)
        placeholder(defaultPic).error(defaultPic)
            //高质量图片 报错：Software rendering doesn't support hardware bitmaps，无法使用硬编
            .allowHardware(false)
    }
}

fun ImageView.loadPicCrop(url: String?) {
    this.load(url) {
        crossfade(true)
            .size(180, 180)
        placeholder(defaultPic).error(defaultPic)
    }
}

suspend fun Uri.loadBitmap( width: Int, height: Int) = run {
    val request = ImageRequest.Builder(appContext)
        .data(this)
        .size(width, height)
        .build()
    appContext.imageLoader.execute(request).drawable?.toBitmap()
}