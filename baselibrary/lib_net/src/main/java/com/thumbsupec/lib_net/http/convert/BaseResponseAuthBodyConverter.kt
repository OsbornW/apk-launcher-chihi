package com.thumbsupec.lib_net.http.convert

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.thumbsupec.lib_net.http.entity.IResponseData
import com.thumbsupec.lib_net.http.entity.ResultEntity
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass

abstract class BaseResponseAuthBodyConverter(val type: Type) :
    Converter<ResponseBody, Any> {

    companion object {
        private const val LIST_EMPTY = "[]"
        private const val MAP_EMPTY = "{}"
        private const val STRING_EMPTY = "\"\""
        private const val NUMBER_ZERO = "0"
        private const val REQUEST_SUCCESS = 200
        private const val REQUEST_FAILED = 201
    }

    var json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        isLenient = true
    }



    abstract fun getResultClass(): KClass<out IResponseData<JsonElement>>

    @OptIn(ExperimentalSerializationApi::class)
    override fun convert(value: ResponseBody): Any? {
        val valueString = value.string()

        // 直接解析 valueString 为 JsonElement
        val jsonElement = Json.parseToJsonElement(valueString)

        // 判断是否包含 code, msg, data
        if (jsonElement is JsonObject) {
            val jsonObject = jsonElement.jsonObject

            // 判断是否包含 "code", "msg", "data" 这几个字段
            val containsRequiredFields = jsonObject.containsKey("code") &&
                    jsonObject.containsKey("msg") &&
                    jsonObject.containsKey("data")

            var result: IResponseData<JsonElement> = json.decodeFromString(
                serializer(getResultClass().java), valueString
            ) as IResponseData<JsonElement>

            if (containsRequiredFields) {
                // 如果包含 code, msg, data，继续处理
                // 进一步处理正常响应
                when (result.getRequestCode()) {
                    REQUEST_SUCCESS -> {
                        val data = result.data()
                        return if (data == null) {
                            json.decodeFromString(serializer(type), checkType(type))
                        } else {
                            json.decodeFromString(serializer(type), json.encodeToString(data))
                        }
                    }
                    else -> throw handlerErrorCode(result.getRequestCode(), result.getRequestMessage())
                }
            }else {
                // 如果不包含这些字段，直接处理为结构错误
                Log.e("chihi_error", "convert: 没有包含code msg data结构")
                val structuredData = mapOf(
                    "code" to REQUEST_SUCCESS, // 使用默认值或者其他逻辑
                    "msg" to "Response structure does not match expected format",
                    "data" to valueString
                )
                val jsonStr = GsonUtils.toJson(structuredData)
                result= json.decodeFromString(serializer(getResultClass().java), jsonStr) as IResponseData<JsonElement>
                val data = result.data()
                return json.decodeFromString(serializer(type), json.encodeToString(data))
            }
        }else {
            // 如果返回的数据不是JsonObject格式，直接处理为结构错误
            throw IllegalArgumentException("返回数据格式不正确")
        }
    }

    abstract fun handlerErrorCode(errorCode: Int, msg: String): Exception


    private fun checkType(type: Type): String {
        return when (type) {
            is ParameterizedType -> {
                when (type.rawType) {
                    List::class.java -> {
                        LIST_EMPTY
                    }

                    else -> {
                        MAP_EMPTY
                    }
                }
            }

            String::class.java -> {
                STRING_EMPTY
            }

            Int::class.java,
            Double::class.java,
            Float::class.java,
            Long::class.java -> {
                NUMBER_ZERO
            }

            else -> {
                MAP_EMPTY
            }
        }
    }
}

