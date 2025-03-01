package com.shudong.lib_base.ext.net

import com.shudong.lib_base.R
import com.shudong.lib_base.ext.net.except.NetOfflineException
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.showErrorToast
import com.shudong.lib_base.ext.stringValue
import com.shudong.lib_base.ext.yes
import com.thumbsupec.lib_net.HttpServerException
import com.thumbsupec.lib_net.di.TOKEN_INVALID_1
import com.thumbsupec.lib_net.di.TOKEN_INVALID_2
import com.thumbsupec.lib_net.di.TOKEN_INVALID_3

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/3 15:11
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */
fun Throwable.handleNetError(handle:()->Unit) {
    when (this) {
        is HttpServerException -> {
            // 自定义异常
            when (code) {
                TOKEN_INVALID_1, TOKEN_INVALID_2, TOKEN_INVALID_3 -> {
                    // Token 失效，需要最退出登录处理
                   // message?.let { showErrorToast(com.shudong.lib_res.R.string.logout.stringValue()) }
                    //exitLogin()
                }
                else ->{
                   /* message?.isNotEmpty()?.yes { showErrorToast(message!!) }
                        ?.otherwise { showErrorToast(com.shudong.lib_res.R.string.server_error.stringValue()) }*/
                    handle.invoke()
                }

            }
        }
        is NetOfflineException->{

        }
        else -> {
            //其他异常
            //showErrorToast(com.shudong.lib_res.R.string.net_error.stringValue())
            handle.invoke()
        }
    }
}