package com.soya.launcher.ext

import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import dalvik.system.DexClassLoader
import java.io.File


fun loadJarAndCreateWebSocketClient(jarPath: String) {
    // 创建 URLClassLoader
    val dexClassLoader =
        DexClassLoader(jarPath, appContext.cacheDir.absolutePath, null, appContext.classLoader)

    try {
        // 使用反射加载类
        val clazz = dexClassLoader.loadClass("com.chihi.ad_lib.AdSdk")

        // 创建类的实例
        val instance = clazz.getDeclaredConstructor().newInstance()

        // 获取 `hello` 方法并传递回调函数
        val method = clazz.getMethod("hello", (Function1::class.java))  // 获取带一个参数的 hello 方法

        // 创建回调函数
        val callback = object : Function1<String, Unit> {
            override fun invoke(p1: String) {
                // 在这里处理回调传递过来的 msg
                "当前返回的result是：$p1".e("chihi_error")
            }
        }

        // 调用 `hello` 方法并传递回调函数
        method.invoke(instance, callback)


    } catch (e: Exception) {
        e.printStackTrace()
        "当前的异常是：：${e.message}".e("chihi_error")
    }
}


fun loadJar() {
    val jarPath = "${appContext.cacheDir.absolutePath}/ad_lib-release.aar"
    loadJarAndCreateWebSocketClient(jarPath)
}

// 使用示例
/*fun main() {
    val jarPath = "${appContext.filesDir.absolutePath}/socket_1.5.7.jar"
    val serverUri = "ws://192.168.1.188:1991"
    loadJarAndCreateWebSocketClient(jarPath, serverUri)
}*/
