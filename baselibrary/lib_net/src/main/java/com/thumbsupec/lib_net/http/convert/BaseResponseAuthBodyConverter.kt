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
        Log.d("chihi_debug", "JSON Response: $valueString")

        return try {
            val jsonElement = Json.parseToJsonElement(valueString)
            if (jsonElement !is JsonObject) {
                throw IllegalArgumentException("Expected a JSON object but got ${jsonElement::class}")
            }

            val jsonObject = jsonElement.jsonObject
            val containsRequiredFields = jsonObject.containsKey("code") &&
                    jsonObject.containsKey("msg") &&
                    jsonObject.containsKey("data")

            val result: IResponseData<JsonElement> = if (containsRequiredFields) {
                json.decodeFromString(serializer(getResultClass().java), valueString) as IResponseData<JsonElement>
            } else {
                Log.e("chihi_error", "Invalid response structure")
                // 构造默认结构
                object : IResponseData<JsonElement> {
                    override fun getRequestCode() = REQUEST_SUCCESS
                    override fun getRequestMessage() = "Response structure does not match expected format"
                    override fun data() = Json.parseToJsonElement(valueString)
                }
            }

            // 解析 data 为目标类型
            val data = result.data()
            if (data == null) {
                json.decodeFromString(serializer(type), checkType(type))
            } else {
                json.decodeFromJsonElement(serializer(type), data)
            }
        } catch (e: Exception) {
            Log.e("chihi_error", "Failed to process JSON response", e)
            throw e
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

