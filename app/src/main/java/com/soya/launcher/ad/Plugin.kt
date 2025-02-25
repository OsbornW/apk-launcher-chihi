package com.soya.launcher.ad

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import com.shudong.lib_base.ext.e
import dalvik.system.DexClassLoader
import java.io.File
import java.lang.ref.WeakReference

object Plugin {

    @Volatile
    private var _dexClassLoader: DexClassLoader? = null
    private var _pluginContext: WeakReference<Context>? = null
    var currentApkPath: String? = null

    // 公共属性，直接访问插件的 DexClassLoader
    val dexClassLoader: DexClassLoader
        get() = _dexClassLoader
            ?: throw IllegalStateException("Plugin is not installed. Call install() first.")

    // 公共属性，直接访问插件的 Context
    val pluginContext: Context
        get() = _pluginContext?.get()
            ?: throw IllegalStateException("Plugin is not installed. Call install() first.")

    /**
     * 安装插件
     * @param context 应用上下文
     * @param apkPath 插件 APK 文件路径
     */
    fun install(context: Context, apkPath: String) {
        synchronized(this) {
            if (apkPath != currentApkPath) {
                _dexClassLoader = createDexClassLoader(context, apkPath)
                _pluginContext = WeakReference(createPluginContext(context, apkPath))
                currentApkPath = apkPath
                initializePlugin()
            }
        }
    }

    /**
     * 卸载插件
     */
    fun uninstall() {
        synchronized(this) {
            _dexClassLoader = null
            _pluginContext?.clear()
            currentApkPath = null
            AdSdk.isAdInitialized = false
        }
    }

    /**
     * 重新安装插件
     */
    fun reinstall(context: Context, apkPath: String) {
        uninstall() // 卸载已有插件
        install(context, apkPath) // 重新安装新的插件
    }

    /**
     * 创建 DexClassLoader
     */
    private fun createDexClassLoader(context: Context, apkPath: String): DexClassLoader {
        val file = File(apkPath)

        if (!file.exists()) {
            throw IllegalArgumentException("APK file does not exist at path: $apkPath")
        }

        file.setReadOnly()
        val optimizedDir = context.getDir("dex", Context.MODE_PRIVATE).absolutePath
        return DexClassLoader(
            file.absolutePath,
            optimizedDir,
            null,
            context.classLoader
        )
    }

    /**
     * 创建插件专属 ContextWrapper
     */
    private fun createPluginContext(context: Context, apkPath: String): ContextWrapper {
        val assetManager = AssetManager::class.java.newInstance()
        val addAssetPathMethod = assetManager::class.java.getMethod("addAssetPath", String::class.java)
        addAssetPathMethod.invoke(assetManager, apkPath)

        val pluginResources = Resources(
            assetManager,
            context.resources.displayMetrics,
            context.resources.configuration
        )

        return object : ContextWrapper(context.applicationContext) {
            override fun getResources(): Resources = pluginResources
            override fun getAssets(): AssetManager = pluginResources.assets
            override fun getTheme(): Resources.Theme {
                val theme = pluginResources.newTheme()
                theme.setTo(context.theme)
                return theme
            }
        }
    }

    /**
     * 初始化插件逻辑
     */
    private fun initializePlugin() {
        try {
            val pluginManagerClass = dexClassLoader.loadClass("com.chihi.adplugin.PluginManager")
            val instanceField = pluginManagerClass.getDeclaredField("INSTANCE")
            val pluginManagerInstance = instanceField.get(null)
            val initMethod = pluginManagerClass.getMethod("init", Context::class.java)
            initMethod.invoke(pluginManagerInstance, pluginContext)
            AdSdk.init()

        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize plugin", e)
        }
    }
}
