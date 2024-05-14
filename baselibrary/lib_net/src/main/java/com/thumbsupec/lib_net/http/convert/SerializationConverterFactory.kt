package com.thumbsupec.lib_net.http.convert

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class SerializationConverterFactory :
        Converter.Factory() {
    companion object {
        @JvmStatic
        fun create(): SerializationConverterFactory {
            return SerializationConverterFactory()
        }
    }

    override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit: Retrofit
    ): Converter<*, RequestBody> {
        return GsonRequestBodyConverter<Any>()
        //return SerializationRequestBodyConverter(type)
    }

    override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return ResponseBodyConverter(type)
    }
}