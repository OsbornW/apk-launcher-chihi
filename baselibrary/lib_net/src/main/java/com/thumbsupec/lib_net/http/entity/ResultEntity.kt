package com.thumbsupec.lib_net.http.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ResultEntity(val code: Int, val msg: String?, val data: JsonElement?) :
    IResponseData<JsonElement> {


    override fun data() = data

    override fun getRequestCode() = code

    override fun getRequestMessage() = msg?:"网络错误"


}