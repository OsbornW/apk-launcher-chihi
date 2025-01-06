package com.soya.launcher.ad

import com.shudong.lib_base.ext.e
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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
    "解压成功".e("chihi_error1")
    return apkFile
}