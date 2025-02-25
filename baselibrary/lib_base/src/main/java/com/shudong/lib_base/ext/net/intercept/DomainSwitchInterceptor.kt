package com.shudong.lib_base.ext.net.intercept

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

class DomainSwitchInterceptor : Interceptor {
    companion object {

        private val currentDomainIndex = AtomicInteger(0)

        @Volatile
        private var successfulDomain: String? = null


        private var isReq: Boolean = true

        fun resetDomainIndex() {
            currentDomainIndex.set(0)
            successfulDomain = null
            AppCacheNet.successfulDomain = ""
            AppCacheNet.randomUrl = "https://localhost/"
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        println("我进来拦截器了啊--------${chain.request().url}")
        if (isReq) {
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
                println("尝试请求域名: $currentDomain (第 ${currentIndex + 1}/${domains.size} 次尝试)")

                val newUrl = buildUrl(currentDomain, originalRequest)
                val updatedRequestBuilder = originalRequest.newBuilder().url(newUrl)

                if (originalRequest.method == "POST" && originalRequest.body != null) {
                    updatedRequestBuilder.method(originalRequest.method, originalRequest.body)
                }

                try {
                    val updatedRequest = updatedRequestBuilder.build()
                    val response = chain.proceed(updatedRequest)
                    if (response.isSuccessful) {
                        successfulDomain = currentDomain
                        AppCacheNet.successfulDomain = currentDomain
                        return response
                    } else {
                        response.close()
                    }
                } catch (e: IOException) {
                    when (e) {
                        is UnknownHostException,
                        is ConnectException,
                        is SocketTimeoutException,
                        is NoRouteToHostException ->{}
                        else -> throw e
                    }
                    lastException = e
                }

                // 如果不是最后一个域名，切换到下一个
                if (currentIndex < domains.size - 1) {
                    if (currentDomainIndex.compareAndSet(currentIndex, currentIndex + 1)) {
                        println("切换到备用域名: ${domains[currentIndex + 1]}")
                    }
                }
            }

            println("所有域名都不可用")
            AppCacheNet.isDomainTryAll = true
            resetDomainIndex()
            isReq = false
            throw lastException ?: IOException("所有域名请求失败")
        } else {
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