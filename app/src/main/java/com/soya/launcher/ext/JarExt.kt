package com.soya.launcher.ext

import android.os.Build
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import dalvik.system.DexClassLoader
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


fun loadJarAndCreateWebSocketClient(jarPath: String) {
    // 创建 URLClassLoader
    val jarFile = File(jarPath)
    val dexClassLoader =
        DexClassLoader(jarPath, appContext.cacheDir.absolutePath, null, appContext.classLoader)

    try {
        // 使用反射加载类
        val clazz = dexClassLoader.loadClass("cn.codezeng.testmyjar.MyLibrary")

        // 创建类的实例
        val instance = clazz.getDeclaredConstructor().newInstance()

        // 调用 `hello` 方法
        val method = clazz.getMethod("hello")
        val result = method.invoke(instance)

        "当前返回的result是：$result".e("zengyue")
        // 打印方法的结果
        println(result)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun loadJar() {
    val jarPath = "${appContext.cacheDir.absolutePath}/testdex.jar"
    loadJarAndCreateWebSocketClient(jarPath)
}

// 使用示例
/*fun main() {
    val jarPath = "${appContext.filesDir.absolutePath}/socket_1.5.7.jar"
    val serverUri = "ws://192.168.1.188:1991"
    loadJarAndCreateWebSocketClient(jarPath, serverUri)
}*/
