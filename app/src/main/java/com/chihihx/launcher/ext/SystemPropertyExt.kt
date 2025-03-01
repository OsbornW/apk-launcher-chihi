package com.chihihx.launcher.ext

// 扩展函数，用于获取系统属性
fun String.systemPropertyValueString( defaultValue: String = ""): String {
    return try {
        val systemProperties = Class.forName("android.os.SystemProperties")
        val getMethod = systemProperties.getMethod("get", String::class.java, String::class.java)
        getMethod.invoke(systemProperties, this, defaultValue) as String
    } catch (e: Exception) {
        defaultValue
    }
}

fun String.setSystemPropertyValueString(value: String, callback: ((Boolean) -> Unit)? = null) {
    try {
        val systemProperties = Class.forName("android.os.SystemProperties")
        val setMethod = systemProperties.getMethod("set", String::class.java, String::class.java)
        setMethod.invoke(systemProperties, this, value)
        callback?.invoke(true) // 如果回调不为空，传递 true 表示成功
    } catch (e: Exception) {
        e.printStackTrace()
        callback?.invoke(false) // 如果回调不为空，传递 false 表示失败
    }
}




fun String.systemPropertyValueBoolean(defaultValue: Boolean = false): Boolean {
    return try {
        val systemProperties = Class.forName("android.os.SystemProperties")
        val getMethod = systemProperties.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
        getMethod.invoke(systemProperties, this, defaultValue) as Boolean
    } catch (e: Exception) {
        defaultValue
    }
}

fun String.setSystemPropertyValueBoolean(value: Boolean, callback: ((Boolean) -> Unit)? = null) {
    try {
        val systemProperties = Class.forName("android.os.SystemProperties")
        val setMethod = systemProperties.getMethod("set", String::class.java, String::class.java)
        setMethod.invoke(systemProperties, this, value.toString())
        callback?.invoke(true) // 如果回调不为空，传递 true 表示成功
    } catch (e: Exception) {
        e.printStackTrace()
        callback?.invoke(false) // 如果回调不为空，传递 false 表示失败
    }
}






const val SYSTEM_PROPERTY_AUTO_RESPONSE = "persist.vendor.auto_focus.hysd"   //自动响应（false为关闭）
const val SYSTEM_PROPERTY_IS_GAME = "vendor.chichi.game"   //是否（true为Game版本）