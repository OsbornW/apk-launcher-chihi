package com.thumbsupec.lib_net

class HttpServerException(val code: Int, message: String) : Exception(message)