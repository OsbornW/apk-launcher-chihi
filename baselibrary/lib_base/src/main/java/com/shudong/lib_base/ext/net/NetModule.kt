package com.thumbsupec.lib_net.di

import android.util.Log
import com.thumbsupec.lib_net.http.MyX509
import com.thumbsupec.lib_net.http.SSL
import com.thumbsupec.lib_net.http.convert.SerializationConverterFactory
import com.thumbsupec.lib_net.http.intercept.AuthorizationInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
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
const val HEADERURL = "/app/api/"

private const val TIME_OUT = 8L
private const val TAG = "zengyue"


const val TOKEN_INVALID_1 = 1011006
const val TOKEN_INVALID_2 = 1011004
const val TOKEN_INVALID_3 = 1011008

val httpLoggingInterceptor = HttpLoggingInterceptor { message -> Log.e(TAG, message) }.also {
    it.level = HttpLoggingInterceptor.Level.BODY
}


val netModule = module {
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(BASE_URL)
            .addConverterFactory(SerializationConverterFactory.create())
            .build()
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor())
            //.addInterceptor(ErrorInterceptor())
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .sslSocketFactory(SSL(MyX509()), MyX509())
            .hostnameVerifier { _, _ -> true }
            .readTimeout(TIME_OUT, TimeUnit.SECONDS).also {
                //if (BuildConfig.DEBUG) {
                    it.addInterceptor(httpLoggingInterceptor)
                //}
            }.build()
    }
}