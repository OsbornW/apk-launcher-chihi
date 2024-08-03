package com.soya.launcher.ext

import android.content.Context
import com.shudong.lib_base.ext.e
import dalvik.system.DexClassLoader
import java.io.File

// AAR 加载扩展函数
fun Context.loadAAR() {
    val aarFile = File(this.filesDir, "ad.aar")
    val dexPath = aarFile.absolutePath
    val optimizedDirectory = this.cacheDir.absolutePath
    DexClassLoaderManager.initialize(dexPath, optimizedDirectory, this.classLoader)
}

// 获取类实例的扩展函数
fun <T : Any> Context.getAARClassInstance(className: String): T? {
    return DexClassLoaderManager.getClassInstance(className) as? T
}

// 调用方法的扩展函数
fun <T : Any> Context.callAARMethod(
    className: String,
    methodName: String,
    vararg args: Any?
): Any? {
    return DexClassLoaderManager.callMethod(className, methodName, *args)
}

// 管理 AAR 加载的单例对象
object DexClassLoaderManager {
    private var dexClassLoader: DexClassLoader? = null

    fun initialize(dexPath: String, optimizedDirectory: String, classLoader: ClassLoader) {
        dexClassLoader = DexClassLoader(dexPath, optimizedDirectory, null, classLoader)
    }

    fun getClassInstance(className: String): Any? {
        val classLoader = dexClassLoader ?: return null
        return try {
            val clazz = classLoader.loadClass(className)
            clazz.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun callMethod(className: String, methodName: String, vararg args: Any?): Any? {
        val classLoader = dexClassLoader ?: return null
        return try {
            val clazz = classLoader.loadClass(className)
            val method = clazz.getMethod(methodName, *args.map { it?.javaClass }.toTypedArray())
            val instance = clazz.getDeclaredConstructor().newInstance()
            method.invoke(instance, *args)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

