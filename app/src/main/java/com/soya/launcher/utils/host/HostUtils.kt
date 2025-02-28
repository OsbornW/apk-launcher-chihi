package com.soya.launcher.utils.host

import com.soya.launcher.utils.host.EncryptUtils.decrypt
import com.thumbsupec.lib_net.AppCacheNet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress

object HostUtils {
    //private const val BASE = "H4sIAAAAAAAAALOUfVcx2/Slgu0yAPoaObsKAAAA" // a0eda9de4e   //TP25003

    private const val BASE = "H4sIAAAAAAAAAMuX2lc/L2t/bcYHAJA1m94KAAAA"  // 775cdf28a3  713  LA27001


    private suspend fun isHostKnown(
        domain: String,
        index: Int,
        callback: (hostDomain: String?) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val address = InetAddress.getByName(domain).hostAddress
            val  isNotNu = address.isNotEmpty()
            callback.invoke("https://$domain/")
            isNotNu
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun incrementString(input: String, increment: Int): String {
        var chars = input.toCharArray()

        for (i in 0 until increment) {
            var carry = true // 进位标志，初始设为 true 表示要进行加一操作

            // 从后往前遍历字符数组
            for (j in chars.indices.reversed()) {
                if (carry) {
                    if (chars[j] == '9') {
                        chars[j] = 'a'
                        carry = false
                    } else if (chars[j] == 'z') {
                        chars[j] = '0'
                        carry = true
                    } else {
                        chars[j]++
                        carry = false
                    }
                } else {
                    break
                }
            }

            // 如果最高位仍有进位，需要在最前面插入一个 '1'
            if (carry) {
                val result = '1'.toString() + String(chars)
                chars = result.toCharArray()
            }
        }

        return String(chars)
    }

    suspend fun getSlaveAvailableHost(callback:((hostDomain:String?)->Unit)) {

         try {
            val base = decrypt(BASE, Constants.HOST_AESKEY)
            var domain: String?
             val startIndex = 0

            // 使用协程并行检查域名
            withContext(Dispatchers.IO) {
                val jobs = mutableListOf<Job>()

                // 从 i ==0 开始，检查 0 到 99
                for (index in 0..99) {
                    val newStartIndex = (startIndex + index) % 100
                    val newBase = incrementString(base, newStartIndex * newStartIndex * (base[0].code + 1))
                    domain = "$newBase.cc"

                    // 异步检查域名
                    jobs.add(launch {
                        if (isHostKnown(domain!!,index,callback)) {
                            jobs.forEach { it.cancel() } // 取消其他任务
                        }
                    })
                }

                // 等待所有任务完成或被取消
                jobs.forEach { it.join() }


            }
        } catch (e: Exception) {

        }
    }

}

