package com.chihihx.launcher.utils.host

import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object IoUtils {
    /**
     * 将输入流数据写入输出流
     * @param is 输入流
     * @param os 输出流
     * @return 写入的数据大小
     * @throws IOException 异常定义
     */
    @Throws(IOException::class)
    fun write(`is`: InputStream, os: OutputStream): Long {
        var length: Long = 0
        var read: Int
        val buffer = ByteArray(8192)
        while (-1 != (`is`.read(buffer).also { read = it })) {
            if (read > 0) {
                os.write(buffer, 0, read)
                length += read.toLong()
            }
        }
        return length
    }

    /**
     * 关闭Closeable对象
     * @param args 对象列表
     */
    fun close(vararg args: Closeable?) {
        if (null != args) {
            for (arg in args) {
                if (null != arg) {
                    try {
                        arg.close()
                    } catch (t: Throwable) {
                        // nothing to do
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    fun getAsString(`is`: InputStream, charset: String): String {
        return String(getAsByteArray(`is`), charset(charset))
    }

    @Throws(IOException::class)
    fun getAsByteArray(`is`: InputStream): ByteArray {
        val output = ByteArrayOutputStream()
        try {
            val buffer = ByteArray(4096)
            var readBytes: Int
            while (-1 != (`is`.read(buffer).also { readBytes = it })) {
                if (readBytes > 0) {
                    output.write(buffer, 0, readBytes)
                }
            }
            return output.toByteArray()
        } finally {
            output.close()
        }
    }
}

