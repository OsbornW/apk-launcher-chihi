package com.thumbsupec.lib_net

import com.drake.serialize.serialize.serialLazy

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

}