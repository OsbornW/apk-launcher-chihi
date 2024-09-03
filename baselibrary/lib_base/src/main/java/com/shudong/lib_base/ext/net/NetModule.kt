package com.thumbsupec.lib_net.di

import android.util.Log
import com.thumbsupec.lib_net.AppCacheNet
import com.thumbsupec.lib_net.http.MyX509
import com.thumbsupec.lib_net.http.MyX509TrustManager
import com.thumbsupec.lib_net.http.SSL
import com.thumbsupec.lib_net.http.convert.SerializationConverterFactory
import com.thumbsupec.lib_net.http.createSslContext
import com.thumbsupec.lib_net.http.getSSLContext
import com.thumbsupec.lib_net.http.intercept.AuthorizationInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/20 17:49
 * @PACKAGE_NAME:  com.thumbsupec.lib_net.di
 */
const val BASE_URL = "http://api.ispruz.com/"
const val BASE_URL_CHECK_CODE = "https://api.freedestop.com/"
const val HEADERURL = ""

private const val TIME_OUT = 8L
private const val TAG = "chihi_zeng"


const val TOKEN_INVALID_1 = 1011006
const val TOKEN_INVALID_2 = 1011004
const val TOKEN_INVALID_3 = 1011008

val httpLoggingInterceptor = HttpLoggingInterceptor { message -> Log.e(TAG, message) }.also {
    it.level = HttpLoggingInterceptor.Level.BODY
}


// 定义 Retrofit 实例用于主域名
val mainRetrofitModule = module {
    single(named("main")) {
        Retrofit.Builder()
            .client(get())
            .baseUrl(AppCacheNet.baseUrl)  // 主域名
            .addConverterFactory(SerializationConverterFactory.create())
            .build()
    }
}

// 定义 Retrofit 实例用于备用域名
val alternateRetrofitModule = module {
    single (named("alternate")){
        Retrofit.Builder()
            .client(get())
            .baseUrl(AppCacheNet.baseUrl)  // 备用域名
            .addConverterFactory(SerializationConverterFactory.create())
            .build()
    }
}

// 定义 Retrofit 实例用于注册码检查激活码
val activeCodeRetrofitModule = module {
    single (named("activeCode")){
        Retrofit.Builder()
            .client(get())
            .baseUrl(BASE_URL_CHECK_CODE)  // 备用域名
            .addConverterFactory(SerializationConverterFactory.create())
            .build()
    }
}

// 定义 OkHttpClient 实例
val httpClientModule = module {
    single {
        val sslContext = getSSLContext()
        val sslSocketFactory = sslContext.socketFactory
        OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor())
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .sslSocketFactory(sslSocketFactory,MyX509TrustManager())
            .hostnameVerifier { _, _ -> true }
            .readTimeout(TIME_OUT, TimeUnit.SECONDS).also {
                it.addInterceptor(httpLoggingInterceptor)
            }.build()
    }
}

val netModules = listOf(mainRetrofitModule, alternateRetrofitModule, activeCodeRetrofitModule, httpClientModule)
