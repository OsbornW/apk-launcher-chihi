package com.soya.launcher.bean

class DivSearch<T>(
    @JvmField val type: Int,
    @JvmField val title: String,
    @JvmField val list: MutableList<T>,
    @JvmField var state: Int
)
