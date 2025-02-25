package com.soya.launcher.utils.host

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.ZipException

/**
 * 字符串压缩解压接口
 *
 * @author zhangheng
 */
object GzipUtils {
    /**
     * 使用GZIP压缩方式，对字节数组数据压缩
     * @param plain 普通数据
     * @return 压缩后数据
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun compress(plain: ByteArray?): ByteArray {
        var output: ByteArrayOutputStream? = null
        var gzOutput: GZIPOutputStream? = null

        try {
            output = ByteArrayOutputStream()
            gzOutput = GZIPOutputStream(output)
            gzOutput.write(plain)
            gzOutput.flush()
            gzOutput.finish()
            return output.toByteArray()
        } catch (e: Exception) {
            throw Exception("compress(gzip) failed: $e", e)
        } finally {
            IoUtils.close(gzOutput, output)
        }
    }

    /**
     * 使用GZIP解压方式，对字节数组数据解压
     * @param buffer 压缩数据
     * @return 解压后数据
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun decompress(buffer: ByteArray): ByteArray {
        return try {
            decompressInternal(buffer)
        } catch (e: Exception) {
            doWhileUngzFailed(e, buffer)
        }
    }

    /**
     * 当解压失败时，试图打印出解压失败的字符串，可能是普通文本
     * @param buffer 解压失败的字符串
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    private fun doWhileUngzFailed(e: Exception, buffer: ByteArray): ByteArray {
        try {
            if ((e is ZipException) && TextUtils.contains(e.message, "GZIP format")) {
                return buffer
            }

            if ((e is IOException) && TextUtils.contains(e.message, "unknown format")) {
                return buffer
            }
        } catch (ex: Exception) {
            // nothing do do
        }
        throw e
    }

    /**
     * 使用GZIP解压方式，对字节数组数据解压
     * @param cipher 压缩数据
     * @return 解压后数据
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    private fun decompressInternal(cipher: ByteArray): ByteArray {
        var input: ByteArrayInputStream? = null
        var output: ByteArrayOutputStream? = null
        var gzInput: GZIPInputStream? = null

        try {
            var length = 0
            val buffer = ByteArray(1024)

            output = ByteArrayOutputStream()
            input = ByteArrayInputStream(cipher)
            gzInput = GZIPInputStream(input)

            while ((gzInput.read(buffer).also { length = it }) > 0) {
                output.write(buffer, 0, length)
            }

            return output.toByteArray()
        } finally {
            IoUtils.close(gzInput, input, output)
        }
    }
}

