@file:OptIn(ExperimentalSerializationApi::class)

package com.chihi.m98.hook

import com.drake.serialize.serialize.SerializeHook
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * 本地序列化存储的数据结构为Json
 */
class JsonSerializeHook : SerializeHook {

    private val json = Json {
        ignoreUnknownKeys = true // JSON和数据模型字段可以不匹配
        coerceInputValues = true // 如果JSON字段是Null则使用默认值
    }

    override fun <T> serialize(name: String, type: Class<T>, data: Any): ByteArray {
        return json.encodeToString(Json.serializersModule.serializer(type), data).toByteArray()
    }

    override fun <T> deserialize(name: String, type: Class<T>, bytes: ByteArray): Any {
        return json.decodeFromString(Json.serializersModule.serializer(type), bytes.decodeToString())
    }
}