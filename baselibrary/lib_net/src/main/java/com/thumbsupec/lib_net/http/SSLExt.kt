package com.thumbsupec.lib_net.http

import android.os.Build
import android.os.Build.VERSION_CODES.M
import javax.net.ssl.TrustManager
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.security.cert.CertificateException

fun createSslContext(): X509TrustManager {

    return MyX509()
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // 使用 MyX509TrustManager 在 API 24 及更高版本
        return MyX509()
    } else {
        // 使用标准的 X509TrustManager 在 API 21 以下版本
        return MyX509TrustManager()
    }*/
}
