package com.soya.launcher.ext

import android.graphics.BitmapFactory
import java.io.File
import android.util.Log
import com.shudong.lib_base.ext.appContext
import com.soya.launcher.bean.HomeInfoDto
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

fun String.getBasePath():String{
    val folderPath = "${appContext.filesDir.absolutePath}/$this"
    val folder = File(folderPath)

    // 创建 header 文件夹（如果不存在）
    if (!folder.exists()) {
        folder.mkdirs()
    }
    return folderPath
}

fun HomeInfoDto.getTotalSize(): Int {
    // 初始化总大小为0
    var totalSize = 0

    // 计算 movies 列表的大小
    val moviesSize = this.movies?.size ?: 0
    totalSize += moviesSize

    // 计算 datas 列表的总大小
    this.movies?.forEach { movy ->
        val name = movy?.name
        val datasSize = movy?.datas?.let { dataList ->
            if (name in listOf("Youtube", "Disney+", "Hulu", "Prime video")) {
                // 如果 name 匹配指定值，则只计算前 8 个元素
                dataList.take(8).size
            } else {
                // 否则计算整个列表的大小
                dataList.size
            }
        } ?: 0
        totalSize += datasSize
    }

    return totalSize
}

fun getImageCount(directoryPath: String): Int {
    val directory = File(directoryPath)
    return if (directory.exists() && directory.isDirectory) {
        directory.listFiles { file -> file.isFile && file.extension in listOf("jpg", "jpeg", "png","webp", "gif") }?.size ?: 0
    } else {
        0
    }
}

fun compareSizes(homeInfo: HomeInfoDto): Boolean {
    val expectedSize = homeInfo.getTotalSize()

    // 计算 header 目录和 content 目录下图片的总数量
    val headerDirPath = "${appContext.filesDir.absolutePath}/header"
    val contentDirPath = "${appContext.filesDir.absolutePath}/content"
    val headerImageCount = getImageCount(headerDirPath)
    val contentImageCount = getImageCount(contentDirPath)

    val actualSize = headerImageCount + contentImageCount

    // 比较计算得到的大小与目录下图片的总数量
    return expectedSize == actualSize
}
