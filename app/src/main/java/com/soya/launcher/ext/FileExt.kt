package com.soya.launcher.ext

import android.graphics.BitmapFactory
import java.io.File
import android.util.Log
import com.shudong.lib_base.ext.appContext
import java.io.IOException

// 扩展函数，用于删除指定路径下的所有图片文件
fun String.deleteAllImages() {
    val imageExtensions = listOf("jpg", "jpeg", "png", "gif") // 支持的图片扩展名
    val directory = File(this)

    if (directory.exists() && directory.isDirectory) {
        directory.listFiles()?.forEach { file ->
            if (file.isFile) {
                val fileExtension = file.extension.lowercase()
                if (fileExtension in imageExtensions) {
                    if (file.delete()) {
                        // 文件成功删除

                    } else {
                        // 文件删除失败

                    }
                }
            }
        }
    } else {

    }
}

fun String.exportToJson(fileName: String = "home.json") {
    try {
        val file = File(appContext.filesDir, fileName)
        file.writeText(this)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun countImagesWithPrefix(prefix: String = "header_"): Int {
    val directory = appContext.filesDir.absolutePath
    val files = File(directory).listFiles { file ->
        file.isFile && file.name.startsWith(prefix) &&
                BitmapFactory.decodeFile(file.absolutePath) != null // 判断是否为有效图片
    }
    return files?.size ?: 0
}