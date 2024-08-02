package com.shudong.lib_base.ext

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/3 10:56
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

/**
 * 对象转 Map
 */
fun <E,F> Any.toMap(): MutableMap<E,F> =
    try {
        GsonUtils.fromJson(
            GsonUtils.toJson(this),
            MutableMap::class.java
        ) as MutableMap<E, F>
    }catch (e:Exception){
        hashMapOf()
    }

fun <E,F> String.toMap(): MutableMap<E,F> =
    try {
        GsonUtils.fromJson(
            this,
            MutableMap::class.java
        ) as MutableMap<E, F>
    }catch (e:Exception){
        "进来解析失败".i("zy1998")
        hashMapOf()
    }



/**
 * Json 转 Bean
 */
inline fun<reified T:Any> String.jsonToBean(): T = GsonUtils.fromJson(this,T::class.java)
inline fun<reified T:Any> T.jsonToString() =  GsonUtils.toJson(this)

inline fun<reified T:Any> String.jsonToTypeBean(): T = run{
    val listType: Type = object : TypeToken<T>() {}.type
    GsonUtils.fromJson(this,listType)
}
