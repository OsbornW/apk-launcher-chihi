package com.thumbsupec.lib_net.http.convert

import com.blankj.utilcode.util.GsonUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Converter
import java.io.IOException

class GsonRequestBodyConverter<T> :
    Converter<T, RequestBody> {
    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val arr =GsonUtils.toJson(value).toByteArray()
        return arr.toRequestBody(MEDIA_TYPE)
    }

    companion object {
        private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
    }
}