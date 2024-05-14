package com.shudong.lib_base.ext.net

import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.base.BaseActivity
import com.shudong.lib_base.base.BaseFragment
import com.shudong.lib_base.ext.yes
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


fun <T> Flow<T>.lifecycle(
    base: BaseFragment<*>,
    errorCallback: ((t: Throwable) -> Unit)? = null,
    callback: T.() -> Unit
) {
    base.lifecycleScope.launch(Dispatchers.Main) {
        this@lifecycle.flowOn(Dispatchers.IO).onCompletion {
        }.catch { t ->

            t.handleNetError{
                errorCallback?.let {
                    errorCallback(t)
                }
            }

        }.collect {
            callback(it)
        }
    }
}



fun <T> Flow<T>.lifecycleLoadingView(
    base: BaseFragment<*>,
    errorCallback: ((t: Throwable) -> Unit)? = null,
    callback: T.() -> Unit
) {
    base.lifecycleScope.launch(Dispatchers.Main) {
        this@lifecycleLoadingView.flowOn(Dispatchers.IO).onStart {
            base.loadingView()
        }.onCompletion {
            base.hideLoading()
        }.catch { t ->
            t.handleNetError{
                errorCallback?.let {
                    errorCallback(t)
                }
            }

        }.collect {
            callback(it)
        }
    }
}



fun <T> Flow<T>.lifecycle(
    base: BaseActivity<*>,
    errorCallback: ((t: Throwable) -> Unit)? = null,
    isShowError:Boolean = true,
    callback: T.() -> Unit
) {
    base.lifecycleScope.launch(Dispatchers.Main) {
        this@lifecycle.flowOn(Dispatchers.IO).onCompletion {
        }.catch { t ->
            isShowError.yes { t.handleNetError{
                errorCallback?.let {
                    errorCallback(t)
                }
            } }

        }.collect {
            callback(it)
        }
    }
}



fun <T> Flow<T>.lifecycleLoadingView(
    base: BaseActivity<*>,
    errorCallback: ((t: Throwable) -> Unit)? = null,
    msg:String = "",
    callback: T.() -> Unit
) {
    base.lifecycleScope.launch(Dispatchers.Main) {
        this@lifecycleLoadingView.flowOn(Dispatchers.IO).onStart {
            base.loadingView(msg)
        }.onCompletion {
            base.hideLoading()
        }.catch { t ->
            t.handleNetError{
                errorCallback?.let {
                    errorCallback(t)
                }
            }

        }.collect {
            callback(it)
        }
    }
}

fun countDownFlow(
    scope: CoroutineScope,
    total: Int,
    onTick: ((Int) -> Unit)?=null,
    onFinish: (() -> Unit)? = null,
): Job {
    return flow {
        for (i in total downTo 0) {
            emit(i)
            delay(1000)
        }
    }.flowOn(Dispatchers.Main)
        .onStart {}
        .onCompletion { onFinish?.invoke() }
        .onEach { onTick?.invoke(it) }
        .launchIn(scope)
}
