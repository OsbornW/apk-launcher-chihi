package com.chihihx.launcher.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

/**
 * 产生uuid, 用以作为设备唯一标识
 * permission needed: android.permission.WRITE_EXTERNAL_STORAGE
 */
object DeviceUuidFactory {
    private val TAG: String = DeviceUuidFactory::class.java.simpleName

    private var deviceUUID: String? = null

    //TODO 不同的应用可能会发生变化
    private const val UUID_FILE_NAME = "kdid"
    private val EXTERNAL_UUID_DIR = (Environment
        .getExternalStorageDirectory()
        .toString() + File.separator
            + UUID_FILE_NAME)

    @JvmStatic
    @Synchronized
    fun getUUID(context: Context): String? {
        if (deviceUUID == null) {
            val internalUUIDFile = File(context.filesDir, UUID_FILE_NAME)
            val externalUUIDFile = File(EXTERNAL_UUID_DIR, UUID_FILE_NAME)
            try {
                // 1.优先从应用目录中读取
                var tempUUID = readUUIDFile(internalUUIDFile)

                // 2. 应用目录中无效，则尝试从SD卡中导入
                if (invalidUUID(tempUUID)) {
                    tempUUID = readUUIDFile(externalUUIDFile)
                    if (validUUID(tempUUID)) {
                        writeUUIDFile(internalUUIDFile, tempUUID)
                    }
                }

                // 3. 从SD卡中导入失败，则重新生成
                if (invalidUUID(tempUUID)) {
                    tempUUID = generateUUID()
                    writeUUIDFile(internalUUIDFile, tempUUID)
                    writeUUIDFile(externalUUIDFile, tempUUID)
                }

                deviceUUID = tempUUID
            } catch (e: Exception) {
                return null
            }
        }

        return deviceUUID
    }

    private fun validUUID(uuid: String?): Boolean {
        return !(uuid == null || "" == uuid.trim { it <= ' ' })

        /*
		if (uuid.length() != 36) { // TODO
			return false;
		}
		*/
    }

    private fun invalidUUID(uuid: String?): Boolean {
        return !validUUID(uuid)
    }

    private fun readUUIDFile(uuidFile: File?): String? {
        if (uuidFile == null || !uuidFile.exists()) {
            return null
        }

        try {
            val f = RandomAccessFile(uuidFile, "r")
            val bytes = ByteArray(f.length().toInt())
            f.readFully(bytes)
            f.close()

            return String(bytes)
        } catch (e: IOException) {
        }

        return null
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    @Throws(IOException::class)
    private fun writeUUIDFile(file: File, u: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Files.write(Paths.get(file.toURI()), u!!.toByteArray())
        } else {
            val outputStream = FileOutputStream(file)
            outputStream.write(u!!.toByteArray())
            outputStream.close()
        }
    }
}