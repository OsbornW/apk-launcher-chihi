package com.shudong.lib_base.ext.photo

import android.text.TextUtils
import android.util.Base64
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


// https://blog.csdn.net/qq_39312146/article/details/129195140

/**
 * 将图片转换成Base64编码的字符串
 */
fun String.imageToBase64(): String? {
    if (TextUtils.isEmpty(this)) {
        return null
    }
    var `is`: InputStream? = null
    var data: ByteArray? = null
    var result: String? = null
    try {
        `is` = FileInputStream(this)
        //创建一个字符流大小的数组。
        data = ByteArray(`is`.available())
        //写入数组
        `is`.read(data)
        //用默认的编码格式进行编码
        result = Base64.encodeToString(data, Base64.NO_CLOSE)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        if (null != `is`) {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return result
}


/**
 * 将Base64编码转换为图片
 * @param base64Str
 * @param path
 * @return true
 */
fun base64ToFile(base64Str: String?, path: String?): Boolean {
    val data = Base64.decode(base64Str, Base64.NO_WRAP)
    for (i in data.indices) {
        if (data[i] < 0) {
            //调整异常数据
            //(data[i] += 256).toByte()
            data[i] = (data[i]+256).toByte()
        }
    }
    var os: OutputStream? = null
    return try {
        os = FileOutputStream(path)
        os.write(data)
        os.flush()
        os.close()
        true
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        false
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}


/**
 *
 * 将音频数据编码为Base64字符串
 */
@Throws(IOException::class)
fun encodeAudioFileToBase64(filePath: String?): String? {
    val bufferedInputStream =
        BufferedInputStream(FileInputStream(filePath))
    val byteArrayOutputStream = ByteArrayOutputStream()
    val buffer = ByteArray(4096)
    var bytesRead: Int
    while (bufferedInputStream.read(buffer).also { bytesRead = it } != -1) {
        byteArrayOutputStream.write(buffer, 0, bytesRead)
    }
    bufferedInputStream.close()
    val audioBytes = byteArrayOutputStream.toByteArray()
    byteArrayOutputStream.close()
    // 将音频数据编码为Base64字符串
    return Base64.encodeToString(audioBytes, Base64.DEFAULT)
}


/**
 *
 * 将Base64字符串解码为音频数据
 */
@Throws(IOException::class)
fun decodeBase64ToAudioFile(base64String: String?, filePath: String?) {
// 将Base64字符串解码为音频数据
    val audioBytes = Base64.decode(base64String, Base64.DEFAULT)
    val fileOutputStream = FileOutputStream(filePath)
    fileOutputStream.write(audioBytes)
    fileOutputStream.close()
}


