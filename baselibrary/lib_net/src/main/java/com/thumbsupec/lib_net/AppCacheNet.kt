package com.thumbsupec.lib_net

import com.drake.serialize.serialize.serial
import com.drake.serialize.serialize.serialLazy
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/26 08:49
 * @PACKAGE_NAME:  com.thumbsupec.lib_base
 */
object AppCacheNet {
    // APP Token
    var token: String? by serialLazy()
    var baseUrl: String by serial("")
    var randomUrl: String by serial("https://localhost/")
    var successfulDomain: String by serial("")
    //var isDomainTryAll: Boolean by serial(false)
    val isDomainTryAll = AtomicBoolean(false) // 使用 AtomicBoolean 替代普通 Boolean

}