package com.thumbsupec.lib_net.http

import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate
import java.security.cert.CertificateException

class MyX509TrustManager : X509TrustManager {
    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // 实现客户端证书验证逻辑
        // 如果验证失败，可以抛出 CertificateException
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // 实现服务器证书验证逻辑
        // 如果验证失败，可以抛出 CertificateException
    }

    override fun getAcceptedIssuers(): Array<X509Certificate>? {
        // 返回受信任的颁发机构的证书列表
        return null
    }
}
