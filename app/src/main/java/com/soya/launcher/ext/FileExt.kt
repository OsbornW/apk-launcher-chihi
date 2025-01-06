package com.soya.launcher.ext

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
    "计算出来的大小:::$expectedSize::::$actualSize".e("zengyue")

    return actualSize>=expectedSize
}


/**
 * 解压 ZIP 文件到当前目录，仅保留解压出的 APK 文件
 * @return 解压后的 APK 文件路径，或者 null 如果解压失败
 */
fun String.unzipAndKeepApk(): File? {
    val zipFile = File(this)
    val outputDir = zipFile.parentFile ?: return null
    var apkFile: File? = null

    try {
        ZipInputStream(FileInputStream(zipFile)).use { zipInputStream ->
            var entry: ZipEntry?
            while (zipInputStream.nextEntry.also { entry = it } != null) {
                val entryName = entry!!.name
                val outputFile = File(outputDir, entryName)
                if (entry!!.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    FileOutputStream(outputFile).use { fos ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (zipInputStream.read(buffer).also { length = it } > 0) {
                            fos.write(buffer, 0, length)
                        }
                    }
                    // 如果是 APK 文件，记录路径
                    if (entryName.endsWith(".apk")) {
                        apkFile = outputFile
                    } else {
                        // 如果不是 APK 文件，删除解压出的文件
                        outputFile.delete()
                    }
                }
                zipInputStream.closeEntry()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        // 删除 ZIP 文件
        zipFile.delete()
    }
    return apkFile
}

