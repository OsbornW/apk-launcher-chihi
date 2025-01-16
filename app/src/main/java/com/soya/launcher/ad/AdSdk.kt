package com.soya.launcher.ad

import android.content.Context
import android.util.Log
import android.view.View
import com.shudong.lib_base.ext.e
import com.soya.launcher.ad.Plugin.dexClassLoader
import com.soya.launcher.ad.config.AdConfig
import com.soya.launcher.ad.config.buildAdCallback
import com.soya.launcher.ad.helper.LoadAdHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy

object AdSdk {

    private var isInitialized = false
    private const val MAX_RETRY_COUNT = 10*60
    private const val RETRY_DELAY_MS = 100L // 每次轮询间隔时间

    /**
     * 初始化SDK
     */
    fun init() {
        try {
            // 1. 加载 AdSdk 类
            val adPluginClass = dexClassLoader.loadClass("com.chihi.adplugin.AdPlugin")

            // 获取目标方法
            val adInitMethod = adPluginClass.getDeclaredMethod("initialize")

            // 方法是实例方法，创建类实例并调用
            val adSdkInstance = adPluginClass.getDeclaredConstructor().newInstance()
            adInitMethod.invoke(adSdkInstance)
            isInitialized = true

        } catch (e: Exception) {
            Log.e("Reflection", "反射调用失败: ${e.message}", e)
        }
    }

    /**
     *
     * 加载广告
     */
    fun loadAd(config: AdConfig.() -> Unit) {

        GlobalScope.launch(Dispatchers.Main) {
            "进来了1".e("chihi_error1")
            "开始加载，是否已经初始化-${ensureInitializedAsync()}".e("chihi_error1")
            if (!ensureInitializedAsync()) return@launch

            try {
                "开始加载广告".e("chihi_error1")
                // 加载 AdPlugin 类
                val adPluginClass = dexClassLoader.loadClass("com.chihi.adplugin.AdPlugin")
                val function1Class =
                    dexClassLoader.loadClass("kotlin.jvm.functions.Function1")

                // 调用 LoadAdHelper 生成配置代理
                val configFunction = LoadAdHelper.createConfigFunction(function1Class, config)

                // 调用 loadAd 方法
                LoadAdHelper.invokeLoadAd(adPluginClass, function1Class, configFunction)
            } catch (e: Exception) {
                Log.e("chihi_error1", "反射调用失败: ${e.message}", e)
            }

        }



    }

    fun removeAd():Boolean {
        if(!isInitialized)return false
        return try {
            // 加载 AdPlugin 类
            val adPluginClass = dexClassLoader.loadClass("com.chihi.adplugin.AdPlugin")

            // 获取 removeAd 方法
            val removeAdMethod = adPluginClass.getDeclaredMethod("removeAd")

            // 创建 AdPlugin 的实例
            val adPluginInstance = adPluginClass.getDeclaredConstructor().newInstance()

            // 调用 removeAd 方法并获取结果
            val result = removeAdMethod.invoke(adPluginInstance) as? Boolean
                ?: throw IllegalStateException("Method removeAd returned null or incorrect type")

            result
        } catch (e: Exception) {
            Log.e("Reflection", "Failed to invoke removeAd: ${e.message}", e)
            false
        }
    }

    /**
     * 确保SDK初始化完成（异步轮询）
     */
    private suspend fun ensureInitializedAsync(): Boolean {
        if (isInitialized) return true

        repeat(MAX_RETRY_COUNT) {
            delay(RETRY_DELAY_MS)
            if (isInitialized) return true
        }

        // 初始化未完成时通知 Application 重新初始化
        Log.e("AdSdk", "SDK 未初始化，通知重新初始化")
        reinitializeCallback?.invoke()
        return false
    }

    private var reinitializeCallback: (() -> Unit)? = null

    fun setReinitializeCallback(callback: () -> Unit) {
        reinitializeCallback = callback
    }

}