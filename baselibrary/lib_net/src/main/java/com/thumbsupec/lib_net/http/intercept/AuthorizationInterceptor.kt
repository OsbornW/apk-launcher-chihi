package com.thumbsupec.lib_net.http.intercept

import com.thumbsupec.lib_net.AppCacheNet.token
import com.thumbsupec.lib_net.isLogin
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        return chain.proceed(request.newBuilder().apply {
            when {
                isLogin() -> {//权限接口

                    val auth = "Bearer ${token}"
                        addHeader("Authorization", auth)

                }
            }

        }.build())
    }
}