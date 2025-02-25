package com.soya.launcher.utils.host

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES加密工具
 *
 */
object AESUtils {
    /**
     * AES加密模式
     */
    private const val MODE = "AES/CFB/NoPadding"

    /**
     * 向量定义
     */
    private val IV = byteArrayOf(
        '0'.code.toByte(),
        '1'.code.toByte(),
        '0'.code.toByte(),
        '2'.code.toByte(),
        '0'.code.toByte(),
        '3'.code.toByte(),
        '0'.code.toByte(),
        '4'.code.toByte(),
        '0'.code.toByte(),
        '5'.code.toByte(),
        '0'.code.toByte(),
        '6'.code.toByte(),
        '0'.code.toByte(),
        '7'.code.toByte(),
        '0'.code.toByte(),
        '8'.code.toByte()
    )

    fun genSalt(): String {
        val t = System.currentTimeMillis()
        return java.lang.Long.toHexString(t)
    }

    /**
     * 使用AES加密，内部对KEY进行MD5编码
     * @param plain 需要加密的内容
     * @param key 密钥固定KEY
     * @param salt 密钥盐码
     * @return 加密后字符串
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun encrypt(plain: String, key: String, salt: String): ByteArray {
        return encrypt(plain.toByteArray(StandardCharsets.UTF_8), key, salt)
    }

    /**
     * 使用AES加密，内部对KEY进行MD5编码
     * @param plain 需要加密的内容
     * @param key 密钥固定KEY
     * @param salt 密钥盐码
     * @return 加密后字符串
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun encrypt(plain: ByteArray?, key: String, salt: String): ByteArray {
        return encrypt(plain, (key + salt))
    }

    /**
     * 使用AES加密，内部对KEY进行MD5编码
     * @param plain 需要加密的内容
     * @param key 加密密码
     * @return 加密后字符串
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun encrypt(plain: String, key: String): ByteArray {
        return encrypt(plain.toByteArray(StandardCharsets.UTF_8), key)
    }

    /**
     * 使用AES加密，内部对KEY进行MD5编码
     * @param plain 需要加密的内容
     * @param key 加密密码
     * @return 加密后字符串
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun encrypt(plain: ByteArray?, key: String): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        val kb = md.digest(key.toByteArray(StandardCharsets.UTF_8))

        val iv = IvParameterSpec(IV)
        val spec = SecretKeySpec(kb, "AESUtils")

        // "算法/模式/补码方式"
        val cipher = Cipher.getInstance(MODE)
        cipher.init(Cipher.ENCRYPT_MODE, spec, iv)

        // 加密
        return cipher.doFinal(plain)
    }

    /**
     * 对AES加密字符串解密
     * @param cipherText 使用AES加密的字符串
     * @param key 加密密码
     * @param salt 密钥盐码
     * @return 解密后明文
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun decryptAsString(cipherText: ByteArray?, key: String, salt: String): String {
        return String(decrypt(cipherText, (key + salt)), StandardCharsets.UTF_8)
    }

    /**
     * 对AES加密字符串解密
     * @param cipherText 使用AES加密的字符串
     * @param key 加密密码
     * @param salt 密钥盐码
     * @return 解密后明文
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun decrypt(cipherText: ByteArray?, key: String, salt: String): ByteArray {
        return decrypt(cipherText, (key + salt))
    }

    /**
     * 对AES加密字符串解密
     * @param cipherText 使用AES加密的字符串
     * @param key 加密密码
     * @return 解密后明文
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun decryptAsString(cipherText: ByteArray?, key: String): String {
        return String(decrypt(cipherText, key), StandardCharsets.UTF_8)
    }

    /**
     * 对AES加密字符串解密
     * @param cipherText 使用AES加密的字符串
     * @param key 加密密码
     * @return 解密后明文
     * @throws Exception 异常定义
     */
    @Throws(Exception::class)
    fun decrypt(cipherText: ByteArray?, key: String): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        val kb = md.digest(key.toByteArray(StandardCharsets.UTF_8))

        val iv = IvParameterSpec(IV)
        val spec = SecretKeySpec(kb, "AESUtils")

        // "算法/模式/补码方式"
        val cipher = Cipher.getInstance(MODE)
        cipher.init(Cipher.DECRYPT_MODE, spec, iv)

        // 解密
        return cipher.doFinal(cipherText)
    }
}

