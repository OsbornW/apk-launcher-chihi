package com.shudong.lib_base.ext

import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.base.BaseActivity
import com.shudong.lib_base.currentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/18 09:30
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */
fun delayExecute(delayTime: Long, opration: () -> Unit) =
    run {
        val activity = currentActivity as BaseActivity<*>
        activity.lifecycleScope.launch {
            delay(delayTime)
            opration.invoke()
        }
    }


fun globalCoroutine(): CoroutineScope {
    val activity = currentActivity as BaseActivity<*>
    return activity.lifecycleScope
}