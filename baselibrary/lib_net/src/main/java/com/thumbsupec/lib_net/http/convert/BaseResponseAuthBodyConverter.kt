package com.thumbsupec.lib_net.http.convert

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.thumbsupec.lib_net.http.entity.IResponseData
import com.thumbsupec.lib_net.http.entity.ResultEntity
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

    fun isValidResponse(response: IResponseData<JsonElement>): Boolean {
        return response.data() != null&& response.getRequestCode() == REQUEST_SUCCESS||response.getRequestCode() == REQUEST_FAILED
    }


    abstract fun getResultClass(): KClass<out IResponseData<JsonElement>>

    override fun convert(value: ResponseBody): Any? {
        val valueString = value.string()

        val kSerializer: KSerializer<Any> = serializer(getResultClass().java)
        var result: IResponseData<JsonElement> =
            json.decodeFromString(kSerializer, valueString) as IResponseData<JsonElement>


        // 检查是否包含 `code`, `msg`, `data` 结构
        if (!isValidResponse(result)) {
            // 如果不包含，将整个返回的数据塞给 `data`
            Log.e("zengyue2", "convert: 没有包含code data结构" )
            val structuredData = mapOf(
                "code" to REQUEST_SUCCESS, // 可以使用默认值或者其他逻辑
                "msg" to "Response structure does not match expected format",
                "data" to valueString
            )

            val jsonStr = GsonUtils.toJson(structuredData)

            result =
                json.decodeFromString(kSerializer, jsonStr) as IResponseData<JsonElement>

            val data = result.data()

            return json.decodeFromString(serializer(type), json.encodeToString(data))
        } else {
            // 正常处理逻辑
            Log.e("zengyue2", "convert: 包含code data结构" )
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
        }
    }

    abstract fun handlerErrorCode(errorCode: Int, msg: String): Exception

    private fun IResponseData<JsonElement>.jsonElementContainsRequiredFields(): Boolean {
        return this.data()?.jsonObject?.let { jsonObject ->
            jsonObject.containsKey("code") && jsonObject.containsKey("msg") && jsonObject.containsKey(
                "data"
            )
        } ?: false
    }

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

