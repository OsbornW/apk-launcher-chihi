package com.shudong.lib_base.ext.net

import okhttp3.Interceptor
import okhttp3.Response

class DynamicBaseUrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        // 根据请求路径来决定使用的 baseUrl
        val someCondition = originalHttpUrl.encodedPath.startsWith("/u/client-path")

        // 根据条件决定使用的 baseUrl
        val newBaseUrl = if (someCondition) {
            "https://api.freedestop.com"
        } else {
            originalHttpUrl.scheme + "://" + originalHttpUrl.host
        }

        // 构建新的 HttpUrl
        val newHttpUrl = originalHttpUrl.newBuilder()
            .scheme("https")
            .host(newBaseUrl.replace("https://", "").replace("http://", ""))
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newHttpUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
