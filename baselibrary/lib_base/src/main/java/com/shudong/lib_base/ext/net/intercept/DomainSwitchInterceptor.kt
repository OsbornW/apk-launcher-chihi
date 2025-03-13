package com.shudong.lib_base.ext.net.intercept

import android.annotation.SuppressLint
import android.os.Build.VERSION_CODES.N
import com.blankj.utilcode.util.NetworkUtils
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.net.except.NetOfflineException
import com.shudong.lib_base.ext.printLog
import com.thumbsupec.lib_net.AppCacheNet
import com.thumbsupec.lib_net.di.domains
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicInteger
import javax.net.ssl.SSLHandshakeException

class DomainSwitchInterceptor : Interceptor {
    companion object {

        private val currentDomainIndex = AtomicInteger(0)

        @Volatile
        private var successfulDomain: String? = null


        fun resetDomainIndex() {
            currentDomainIndex.set(0)
            successfulDomain = null
            AppCacheNet.successfulDomain = ""
            AppCacheNet.randomUrl = "https://localhost/"
        }
    }

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        "拦截到请求----${chain.request().url}".printLog()
        if (!NetworkUtils.isConnected()) throw NetOfflineException("当前网络没有连接")
        if (!AppCacheNet.isDomainTryAll.get()) {
            val originalRequest = chain.request()
            // 如果有 successfulDomain，先尝试使用它
            if (AppCacheNet.successfulDomain.isNotEmpty()) successfulDomain =
                AppCacheNet.successfulDomain
            successfulDomain?.let { domain ->
                val newUrl = buildUrl(domain, originalRequest)
                val updatedRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .apply {
                        if (originalRequest.method == "POST" && originalRequest.body != null) {
                            method(originalRequest.method, originalRequest.body)
                        }
                    }
                    .build()
                try {
                    val response = chain.proceed(updatedRequest)
                    if (response.isSuccessful) {
                        return response
                    } else {
                        response.close()
                    }
                } catch (e: IOException) {
                    when (e) {
                        is UnknownHostException,
                        is ConnectException,
                        is SocketTimeoutException,
                        is SSLHandshakeException,
                        is NoRouteToHostException -> {
                            resetDomainIndex()
                        }

                        else -> throw e
                    }
                }
            }

            // 域名切换逻辑
            var lastException: IOException? = null

            // 限制循环范围为 domains 的索引
            for (index in 0 until domains.size) {
                val currentIndex = currentDomainIndex.get()
                if (currentIndex >= domains.size) {
                    break
                }

                val currentDomain = domains[currentIndex]
                "尝试请求域名: $currentDomain (第 ${currentIndex + 1}/${domains.size} 次尝试)".printLog()

                val newUrl = buildUrl(currentDomain, originalRequest)
                val updatedRequestBuilder = originalRequest.newBuilder().url(newUrl)

                if (originalRequest.method == "POST" && originalRequest.body != null) {
                    updatedRequestBuilder.method(originalRequest.method, originalRequest.body)
                }

                try {
                    val updatedRequest = updatedRequestBuilder.build()
                    val response = chain.proceed(updatedRequest)
                    if (response.isSuccessful) {
                        "请求成功---${response.request.url}".printLog()
                        successfulDomain = currentDomain
                        AppCacheNet.successfulDomain = currentDomain
                        return response
                    } else {
                        "请求失败---${response.request.url}".printLog()
                        response.close()
                    }
                } catch (e: IOException) {
                    "请求抛出异常---${e.message}".printLog()
                    when (e) {
                        is UnknownHostException,
                        is ConnectException,
                        is SocketTimeoutException,
                        is SSLHandshakeException,
                        is NoRouteToHostException -> {
                        }

                        else -> throw e
                    }
                    lastException = e
                }

                // 如果不是最后一个域名，切换到下一个
                if (currentIndex < domains.size - 1) {
                    if (currentDomainIndex.compareAndSet(currentIndex, currentIndex + 1)) {
                        "切换到备用域名: ${domains[currentIndex + 1]}".printLog()
                    }
                }
            }

            "所有域名都不可用".printLog()
            AppCacheNet.isDomainTryAll.compareAndSet(false, true) // 只有当前为 false 时才设为 true
            resetDomainIndex()
            "最终的异常是：---${lastException?.message}".printLog()
            throw lastException ?: IOException("所有域名请求失败")
        } else {
            "所有域名请求失败".printLog()
            throw IOException("所有域名请求失败")
        }


    }

    private fun buildUrl(domain: String, originalRequest: okhttp3.Request): String {
        val encodedPath = originalRequest.url.encodedPath
        val baseUrl = if (domain.endsWith("/")) domain.dropLast(1) else domain
        val newUrl = baseUrl + encodedPath
        return if (originalRequest.url.encodedQuery.isNullOrEmpty()) {
            newUrl
        } else {
            "$newUrl?${originalRequest.url.encodedQuery}"
        }
    }
}