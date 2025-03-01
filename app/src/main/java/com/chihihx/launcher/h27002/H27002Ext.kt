package com.chihihx.launcher.h27002

import android.content.Context
import android.graphics.drawable.Drawable
import com.google.gson.Gson
import com.chihihx.launcher.bean.HomeResponse
import com.chihihx.launcher.manager.FilePathMangaer
import java.io.FileReader

fun Context.defaultResource(): HomeResponse.Inner?{
    try {
        val data = Gson().fromJson(
            FileReader(
                FilePathMangaer.getMoviePath(
                    this
                ) + "/data/movie.json"
            ), HomeResponse.Inner::class.java
        )
       return data


    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

// 扩展函数，Context 上的扩展函数用于将文件路径转换为 Drawable
fun Context.getDrawableFromPath(path: String): Drawable? {
    return Drawable.createFromPath(path)
}

// 扩展函数，Context 上的扩展函数用于根据资源名称获取 Drawable
fun Context.getDrawableByName(name: String): Drawable? {
    val resId = resources.getIdentifier(name, "drawable", packageName)
    return if (resId != 0) {
        resources.getDrawable(resId, null)
    } else {
        null
    }
}

