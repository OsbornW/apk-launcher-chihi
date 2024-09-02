package com.thumbsupec.lib_net.http.convert

import com.thumbsupec.lib_net.HttpServerException
import com.thumbsupec.lib_net.http.entity.IResponseData
import com.thumbsupec.lib_net.http.entity.ResultEntity
import kotlinx.serialization.json.JsonElement
import java.lang.reflect.Type
import kotlin.reflect.KClass


class ResponseBodyConverter(type: Type) :
    BaseResponseAuthBodyConverter(type) {


    override fun getResultClass(): KClass<out IResponseData<JsonElement>> {
        return ResultEntity::class
    }

    override fun handlerErrorCode(errorCode: Int, msg: String): Exception {
        return HttpServerException(errorCode,msg)
    }
}