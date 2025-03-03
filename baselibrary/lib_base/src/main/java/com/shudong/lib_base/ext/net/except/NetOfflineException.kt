package com.shudong.lib_base.ext.net.except

import java.io.IOException

// 定义一个自定义异常类
class NetOfflineException(message: String) : IOException(message)