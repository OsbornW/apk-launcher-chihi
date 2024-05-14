package com.thumbsupec.lib_net.http.convert

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Converter
import java.lang.reflect.Type

class SerializationRequestBodyConverter(
         @Serializable val type: Type
) : Converter<Any, RequestBody> {
    companion object {
        private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
    }

    var json: Json = Json
    override fun convert(value: Any): RequestBody {
        val requestBodyString = json.encodeToString(serializer(type), value)
        val buffer = Buffer()
        buffer.writeString(requestBodyString, Charsets.UTF_8)
        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }
}