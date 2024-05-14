package com.thumbsupec.lib_net

import com.thumbsupec.lib_net.AppCacheNet.token

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/3 14:33
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

/**
 * 当前的登录状态
 */
fun isLogin () = !token.isNullOrEmpty()
fun cleanLoginToken() {token = ""}

