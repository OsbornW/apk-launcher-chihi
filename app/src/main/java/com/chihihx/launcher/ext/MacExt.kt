package com.chihihx.launcher.ext

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

// 扩展函数：读取文件内容为 String
@Throws(IOException::class)
fun File.loadAsString(): String {
    val fileData = StringBuffer(1000)
    BufferedReader(FileReader(this)).use { reader ->
        val buf = CharArray(1024)
        var numRead: Int
        while (reader.read(buf).also { numRead = it } != -1) {
            val readData = String(buf, 0, numRead)
            fileData.append(readData)
        }
    }
    return fileData.toString()
}

// 扩展函数：获取 STB 的 MAC 地址
fun getMacAddress(): String? {
    return try {
        val macFile = File("/sys/class/net/eth0/address")
        macFile.loadAsString().trim().uppercase().substring(0, 17)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
