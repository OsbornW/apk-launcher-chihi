package com.chihihx.launcher.utils.host

import android.util.Base64
import java.nio.charset.StandardCharsets

object EncryptUtils {
    fun decrypt(encryptedList: List<String>?, key: String): List<String> {
        val decryptedList: MutableList<String> = ArrayList()
        if (null != encryptedList) {
            for (encrypted in encryptedList) {
                decryptedList.add(decrypt(encrypted, key))
            }
        }
        return decryptedList
    }

    fun decrypt(encrypted: String, key: String): String {
        try {
            val byteZip = Base64.decode(encrypted, Base64.NO_WRAP)
            val byteEncrypted = GzipUtils.decompress(byteZip)
            val byteDecrypted = AESUtils.decrypt(byteEncrypted, key)
            val str = String(byteDecrypted, StandardCharsets.UTF_8)
            return str
        } catch (e: Exception) {
            return encrypted
        }
    }

    fun encrypt(plainList: List<String>?, key: String): List<String?> {
        val encryptedList: MutableList<String?> = ArrayList()
        if (null != plainList) {
            for (plain in plainList) {
                encryptedList.add(encrypt(plain, key))
            }
        }
        return encryptedList
    }

    fun encrypt(plain: String, key: String): String {
        try {
            val byteEncrypted = AESUtils.encrypt(plain, key)
            val byteZip = GzipUtils.compress(byteEncrypted)
            val byteBase64 = Base64.encode(byteZip, Base64.NO_WRAP)
            return String(byteBase64, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            return plain
        }
    }
}

