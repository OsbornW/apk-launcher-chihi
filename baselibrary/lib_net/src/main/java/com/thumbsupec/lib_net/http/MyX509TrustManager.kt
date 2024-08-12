package com.thumbsupec.lib_net.http

import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate
import java.security.cert.CertificateException

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory

class MyX509TrustManager : X509TrustManager {

    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // 这里可以实现自定义客户端证书检查逻辑
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // 这里可以实现自定义服务器证书检查逻辑
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }
}

fun getSSLContext(): SSLContext {
    val trustManager = MyX509TrustManager()
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, arrayOf(trustManager), null)
    return sslContext
}

