package com.chihihx.launcher.ad

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.ext.appContext
import com.chihihx.launcher.ad.Plugin.dexClassLoader
import com.chihihx.launcher.ad.config.AdConfig
import com.chihihx.launcher.ad.config.PluginCache
import com.chihihx.launcher.ad.config.buildAdCallback
import com.chihihx.launcher.ad.constants.ClassNameConstants.AD_PLUGIN_PATH
import com.chihihx.launcher.ad.helper.LoadAdHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

object AdSdk {

    var isAdInitialized = false
    private const val MAX_RETRY_COUNT = 12
    private const val RETRY_DELAY_MS = 800L // 每次轮询间隔时间

    /**
     * 初始化SDK
     */
    fun init() {
        try {
            // 1. 加载 AdSdk 类
            val adPluginClass = dexClassLoader.loadClass(AD_PLUGIN_PATH)

            // 获取目标方法
            val adInitMethod = adPluginClass.getDeclaredMethod("initialize")

            // 方法是实例方法，创建类实例并调用
            val adSdkInstance = adPluginClass.getDeclaredConstructor().newInstance()
            adInitMethod.invoke(adSdkInstance)
            PluginCache.pluginLang = Locale.getDefault().language
            isAdInitialized = true

        } catch (e: Exception) {
        }
    }

    /**
     *
     * 加载广告
     */
    private fun loadAdContent(config: AdConfig.() -> Unit) {

        val (adConfig, callback) = buildAdCallback(config)
        try {

            if (PluginCache.pluginLang != Locale.getDefault().language) {
                Plugin.reinstall(appContext, PluginCache.pluginPath)
                callback.onAdCountdownFinished()
            } else {
                // 加载 AdPlugin 类
                val adPluginClass = dexClassLoader.loadClass(AD_PLUGIN_PATH)
                val function1Class =
                    dexClassLoader.loadClass("kotlin.jvm.functions.Function1")

                // 调用 LoadAdHelper 生成配置代理
                val configFunction = LoadAdHelper.createConfigFunction(function1Class, config)

                // 调用 loadAd 方法
                LoadAdHelper.invokeLoadAd(adPluginClass, function1Class, configFunction)
            }

        } catch (e: Exception) {
            callback.onAdLoadFailed(e.message.toString())
        }


    }

    fun LifecycleOwner.loadAd(config: AdConfig.() -> Unit) {
        this.lifecycleScope.launch {
            repeat(MAX_RETRY_COUNT) {
                delay(RETRY_DELAY_MS)
                if (isAdInitialized) {
                    loadAdContent(config)
                    return@launch
                }
            }
        }

    }

    fun removeAd(): Boolean {
        if (!isAdInitialized) return false
        return try {
            // 加载 AdPlugin 类
            val adPluginClass = dexClassLoader.loadClass(AD_PLUGIN_PATH)

            // 获取 removeAd 方法
            val removeAdMethod = adPluginClass.getDeclaredMethod("removeAd")

            // 创建 AdPlugin 的实例
            val adPluginInstance = adPluginClass.getDeclaredConstructor().newInstance()

            // 调用 removeAd 方法并获取结果
            val result = removeAdMethod.invoke(adPluginInstance) as? Boolean
                ?: throw IllegalStateException("Method removeAd returned null or incorrect type")

            result
        } catch (e: Exception) {
            false
        }
    }


}