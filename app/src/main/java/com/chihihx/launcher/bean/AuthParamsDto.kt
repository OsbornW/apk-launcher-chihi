package com.chihihx.launcher.bean

import com.blankj.utilcode.util.DeviceUtils
import com.shudong.lib_base.global.AppCacheBase.activeCode
import com.chihihx.launcher.BuildConfig
import com.chihihx.launcher.utils.md5
import kotlinx.serialization.Serializable

/**
 * 唯一ID
 */
fun getUniqueID() =
    DeviceUtils.getUniqueDeviceId().subSequence(0, DeviceUtils.getUniqueDeviceId().length - 1)
        .toString()

fun getVersionValue() = 1003    //版本值
fun getChannelId() = BuildConfig.CHANNEL    //渠道ID
fun getChildChannel() = "S10001"    //子渠道
fun getTimeStamp() = System.currentTimeMillis() / 1000      //时间戳

/**
 *
 * 加密后的MD5值
 */
fun getMD5String() = run {
    // 密码盐
    val pwd = "TKPCpTVZUvrI"
    // 待加密的字符串（(d+e+c+b+a+f+密码盐）
    val toBeEncryptedString = "${getChannelId()}${getChildChannel()}" +
            "${getVersionValue()}$activeCode${getUniqueID()}" +
            "${getTimeStamp()}$pwd"
    // 对字符串进行MD5加密
    val md5String = toBeEncryptedString.md5()
    md5String
}

@Serializable
data class AuthParamsDto(
    val b: String,
    val a: String = getUniqueID(),
    val c: Int = getVersionValue(),
    val d: String = getChannelId(),
    val e: String = getChildChannel(),
    val f: Long = getTimeStamp(),
    val s: String = getMD5String(),
)
