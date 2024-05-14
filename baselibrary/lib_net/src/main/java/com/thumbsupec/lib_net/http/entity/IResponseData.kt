package com.thumbsupec.lib_net.http.entity

import kotlinx.serialization.json.JsonElement

interface IResponseData<T : JsonElement> {
    /**
     * 返回数据
     * @return T
     */
    fun data(): T?


    /**
     * 返回错误码
     * @return String
     */
    fun getRequestCode(): Int

    /**
     * 错误信息
     * @return String
     */
    fun getRequestMessage(): String
}