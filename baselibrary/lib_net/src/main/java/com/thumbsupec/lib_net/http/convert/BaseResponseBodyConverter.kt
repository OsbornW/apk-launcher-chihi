package com.thumbsupec.lib_net.http.convert

import com.thumbsupec.lib_net.http.entity.IResponseData
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass

open abstract class BaseResponseBodyConverter(val type: Type) :
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
       // coerceInputValues = true
        encodeDefaults = true
        //序列化是是否忽略null
        explicitNulls = false
        //是否宽松解析，JSON格式错误时也可以解析
        isLenient = true
    }

    abstract fun getResultClass(): KClass<out IResponseData<JsonElement>>

    override fun convert(value: ResponseBody): Any? {
        val valueString = value.string()
        //val valueString = "{\"success\":true,\"code\":200,\"message\":\"Request Succeeded\",\"data\":null}"
        val kSerializer: KSerializer<Any> =
                serializer(getResultClass().java)
        val result: IResponseData<JsonElement> =
                json.decodeFromString(kSerializer, valueString) as IResponseData<JsonElement>
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
