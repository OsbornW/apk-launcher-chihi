package com.soya.launcher.ad.helper

import android.util.Log
import com.shudong.lib_base.ext.e
import com.soya.launcher.ad.Plugin
import com.soya.launcher.ad.api.impl.AdLoadCallbackBuilder
import com.soya.launcher.ad.config.AdConfig
import com.soya.launcher.ad.config.buildAdCallback
import java.lang.reflect.Proxy

object LoadAdHelper {

    // 创建配置代理
    fun createConfigFunction(function1Class: Class<*>, config: AdConfig.() -> Unit): Any {
        return Proxy.newProxyInstance(
            Plugin.dexClassLoader,
            arrayOf(function1Class)
        ) { _, method, args ->
            if (method.name == "invoke" && args.isNotEmpty()) {
                val adConfig = args[0]
                updateAdConfig(adConfig, config)
            }
            null
        }
    }

    // 更新 AdConfig
    private fun updateAdConfig(adConfig: Any, config: AdConfig.() -> Unit) {
        try {
            val (launcherConfig,callback) = buildAdCallback(config)
            val adView = launcherConfig.adView
            val adId = launcherConfig.adId
            val adConfigClass = Plugin.dexClassLoader.loadClass("com.chihi.adplugin.AdConfig")

            // 使用统一方法来更新字段
            updateFieldIfNotNull(Pair(adConfig,adConfigClass), "adView", adView)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"adId", adId)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"onAdCallback", createAdCallback(callback))

            // 其他控制字段
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"isClosable", launcherConfig.isClosable)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"adSkipTime", launcherConfig.adSkipTime)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"position", launcherConfig.position)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"floatingWidth", launcherConfig.floatingWidth)
            updateFieldIfNotNull(Pair(adConfig,adConfigClass), "floatingHeight", launcherConfig.floatingHeight)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"floatingX", launcherConfig.floatingX)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"floatingY", launcherConfig.floatingY)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"isCountdownVisible", launcherConfig.isCountdownVisible)

            // 其他字段设置可以继续根据需要扩展
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"apiKey", launcherConfig.apiKey)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"isLoadFromLocal", launcherConfig.isLoadFromLocal)
            updateFieldIfNotNull( Pair(adConfig,adConfigClass),"isAutoFocus", launcherConfig.isAutoFocus)

        } catch (e: Exception) {
            Log.e("LoadAdHelper", "更新 AdConfig 失败: ${e.message}", e)
        }
    }

    // 创建广告回调
    private fun createAdCallback(callback: AdLoadCallbackBuilder): Any {
        val function1Class = Plugin.dexClassLoader.loadClass("kotlin.jvm.functions.Function1")

        return Proxy.newProxyInstance(
            Plugin.dexClassLoader,
            arrayOf(function1Class)
        ) { _, method, args ->
            if (method.name == "invoke" && args.isNotEmpty()) {
                val arg = args[0]
                invokeAdCallbacks(arg,callback)
            }
            null
        }
    }

    // 执行广告回调方法
    private fun invokeAdCallbacks(arg: Any, callback: AdLoadCallbackBuilder) {
        try {
            val builderClass = arg.javaClass

            // onAdDataFetchStart 回调
            val fetchStartMethod = builderClass.getDeclaredMethod(
                "onAdDataFetchStart",
                Plugin.dexClassLoader.loadClass("kotlin.jvm.functions.Function0")
            )
            val fetchStartCallback = createFunctionCallback(callback,
                "Function0","onAdDataFetchStart",
                "广告数据开始加载")
            fetchStartMethod.invoke(arg, fetchStartCallback)

            // onAdDataFetchSuccess 回调
            val fetchSuccessMethod = builderClass.getDeclaredMethod(
                "onAdDataFetchSuccess",
                Plugin.dexClassLoader.loadClass("kotlin.jvm.functions.Function0")
            )
            val fetchSuccessCallback = createFunctionCallback(callback,
                "Function0","onAdDataFetchSuccess",
                "广告数据加载成功")
            fetchSuccessMethod.invoke(arg, fetchSuccessCallback)

            // onAdLoadFailed 回调
            val fetchFailedMethod = builderClass.getDeclaredMethod(
                "onAdLoadFailed",
                Plugin.dexClassLoader.loadClass("kotlin.jvm.functions.Function1")
            )
            val fetchFailedCallback = createFunctionCallback(callback,
                "Function1",
                "onAdLoadFailed","广告加载失败")
            fetchFailedMethod.invoke(arg, fetchFailedCallback)

            val countdownFinishedMethod = builderClass.getDeclaredMethod(
                "onAdCountdownFinished",
                Plugin.dexClassLoader.loadClass("kotlin.jvm.functions.Function0")
            )
            val countdownFinishedCallback = createFunctionCallback(callback,
                "Function0",
                "onAdCountdownFinished","广告倒计时结束")
            countdownFinishedMethod.invoke(arg, countdownFinishedCallback)

            val noLocalAdMethod = builderClass.getDeclaredMethod(
                "onNoLocalAd",
                Plugin.dexClassLoader.loadClass("kotlin.jvm.functions.Function0")
            )
            val noLocalAdCallback = createFunctionCallback(callback,
                "Function0",
                "onNoLocalAd","没有本地缓存的广告")
            noLocalAdMethod.invoke(arg, noLocalAdCallback)


            val adLoadSuccessMethod = builderClass.getDeclaredMethod(
                "onAdLoadSuccess",
                Plugin.dexClassLoader.loadClass("kotlin.jvm.functions.Function0")
            )
            val adLoadSuccessCallback = createFunctionCallback(callback,
                "Function0","onAdLoadSuccess","广告数据加载成功")
            adLoadSuccessMethod.invoke(arg, adLoadSuccessCallback)



        } catch (e: Exception) {
            Log.e("LoadAdHelper", "执行广告回调失败: ${e.message}", e)
        }
    }

    // 创建 Function0 回调
    private fun createFunctionCallback(
        callback: AdLoadCallbackBuilder,
        functionType: String, // 回调类型，例如 Function0 或 Function1
        callbackType: String, // 自定义回调标识
        logMessage: String,   // 日志信息
    ): Any {
        val functionClass = if (functionType == "Function0") {
            Plugin.dexClassLoader.loadClass("kotlin.jvm.functions.Function0")
        } else {
            Plugin.dexClassLoader.loadClass("kotlin.jvm.functions.Function1")
        }

        return Proxy.newProxyInstance(
            Plugin.dexClassLoader,
            arrayOf(functionClass)
        ) { _, method, args ->
            "没有进来任何回调吗？？？${callbackType}".e("chihi_error1")
            when (callbackType) {
                "onAdDataFetchStart" -> {
                    println("回调类型: $callbackType, 消息: $logMessage")
                    // 处理无参数回调逻辑
                    callback.onAdDataFetchStart()
                }
                "onAdLoadFailed" -> {
                    println("回调类型: $callbackType, 消息: $logMessage")
                    if (args != null && args.isNotEmpty()) {
                        val errorMsg = args[0] as? String
                        println("广告加载失败，错误信息: $errorMsg")
                        callback.onAdLoadFailed(errorMsg?:"")
                    }
                }
                "onAdDataFetchSuccess"->{
                    callback.onAdDataFetchSuccess()
                }
                "onAdCountdownFinished"->{
                    callback.onAdCountdownFinished()
                }

                "onNoLocalAd"->{
                    callback.onNoLocalAd()
                }

                "onAdLoadSuccess"->{
                    callback.onAdLoadSuccess()
                }
                else -> {
                    println("未知回调类型: $callbackType, 消息: $logMessage")
                }
            }
            null
        }
    }


    // 调用 loadAd 方法
    fun invokeLoadAd(
        adPluginClass: Class<*>,
        function1Class: Class<*>,
        configFunction: Any
    ) {
        try {
            val loadAdMethod = adPluginClass.getMethod("loadAd", function1Class)
            val adSdkInstance = adPluginClass.getDeclaredConstructor().newInstance()
            loadAdMethod.invoke(adSdkInstance, configFunction)
            Log.d("LoadAdHelper", "loadAd 调用成功")
        } catch (e: Exception) {
            Log.e("LoadAdHelper", "调用 loadAd 失败: ${e.message}", e)
        }
    }


    // 辅助方法：如果字段值不为空，则更新
    private fun updateFieldIfNotNull(config:Pair<Any,Class<*>>, fieldName: String, fieldValue: Any?) {
        fieldValue?.let {
            try {
                val(adConfig,adConfigClass) = config
                val field = adConfigClass.getDeclaredField(fieldName)
                field.isAccessible = true
                field.set(adConfig, fieldValue)
            } catch (e: NoSuchFieldException) {
                Log.e("chihi_error1", "未找到字段: $fieldName", e)
            } catch (e: IllegalAccessException) {
                Log.e("chihi_error1", "无法访问字段: $fieldName", e)
            }
        }
    }

}
