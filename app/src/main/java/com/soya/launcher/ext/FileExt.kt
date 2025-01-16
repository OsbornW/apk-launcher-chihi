package com.soya.launcher.ext

import android.content.Context
import android.graphics.BitmapFactory
import java.io.File
import android.util.Log
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.soya.launcher.bean.HomeInfoDto
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

// 扩展函数，用于删除指定路径下的所有图片文件
fun String.deleteAllImages() {
    val imageExtensions = listOf("jpg", "jpeg", "png", "gif","json") // 支持的图片扩展名
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


fun deleteApkFilesInPluginDir(): Boolean {
    val pluginDir = File(appContext.filesDir, "plugin")
    if (pluginDir.exists() && pluginDir.isDirectory) {
        // 获取目录下所有 .apk 文件
        val apkFiles = pluginDir.listFiles { file ->
            file.extension == "apk"
        }

        // 删除每个 .apk 文件
        apkFiles?.forEach { file ->
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    Log.d("FileDeletion", "Deleted: ${file.absolutePath}")
                } else {
                    Log.e("FileDeletion", "Failed to delete: ${file.absolutePath}")
                }
            }
        }
        return true
    } else {
        Log.e("FileDeletion", "Plugin directory does not exist or is not a directory.")
    }
    return false
}

/**
 * 删除 filesDir 下的 ad 和 plugin 目录
 *
 * @param appContext 应用的 Context
 */
fun deleteAdAndPluginDirectories() {
    val filesDir = appContext.filesDir
    val adDir = File(filesDir, "ad")
    val pluginDir = File(filesDir, "plugin")

    if (adDir.exists() && deleteDirectory(adDir)) {
        println("ad 目录及其内容删除成功")
    } else {
        println("ad 目录删除失败或不存在")
    }

    if (pluginDir.exists() && deleteDirectory(pluginDir)) {
        println("plugin 目录及其内容删除成功")
    } else {
        println("plugin 目录删除失败或不存在")
    }
}

/**
 * 删除指定目录及其内容
 *
 * @param dir 要删除的目录
 * @return 是否删除成功
 */
fun deleteDirectory(dir: File): Boolean {
    if (dir.exists()) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectory(file) // 递归删除子目录
            } else {
                file.delete() // 删除文件
            }
        }
    }
    return dir.delete() // 删除空目录
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

/*fun HomeInfoDto.getTotalSize(): Int {
    var totalSize = 0

    // 计算 datas 列表的大小
    this.movies?.forEach { movy ->
        val name = movy?.name
        val datasSize = movy?.datas?.let { dataList ->
            if (name in listOf("Youtube", "Disney+", "Hulu", "Prime video")) {
                // 如果 name 匹配指定值，则只计算前 8 个元素
                dataList.take(8).size
            } else if (name in listOf("Google play", "media center")) {
                // 特定的 movies 名称不计算 datas 大小
                0
            } else {
                // 否则计算整个列表的大小
                dataList.size
            }
        } ?: 0

        // 打印调试信息
        Log.d("Debug", "Movie: $name, Data Size: $datasSize")

        totalSize += datasSize
    }

    // 打印最终计算结果
    Log.d("Debug", "Total Size: $totalSize")

    return totalSize
}*/


fun HomeInfoDto.getTotalSize(): Int {
    // 初始化总大小为0
    var totalSize = 0

    // 计算 movies 列表的大小
    val moviesSize = this.movies?.size ?: 0
    totalSize += moviesSize

    this.movies?.forEach {
        if(it?.name in listOf("Google play","media center")){
            --totalSize
        }
    }

    // 计算 datas 列表的总大小
    this.movies?.forEach { movy ->
        val name = movy?.name
        val datasSize = movy?.datas?.let { dataList ->
            if (name in listOf("Youtube", "Disney+", "Hulu", "Prime video")) {
                // 如果 name 匹配指定值，则只计算前 8 个元素
                dataList.take(8).size
            }else if(name in listOf("Google play","media center")){
                0
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
        directory.listFiles { file ->
            file.isFile && file.extension in
                    listOf("jpg", "jpeg", "png","webp", "gif") }?.size ?: 0
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
    "计算出来的大小:::$expectedSize::::$actualSize".e("zengyue")

    return actualSize>=expectedSize
}




