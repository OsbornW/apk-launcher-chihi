package com.shudong.lib_base.callback_kt

import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder

open class BannerAdapter<T>(mData: MutableList<T>?) : BannerImageAdapter<T>(mData) {
    private var listener: ((holder: BannerImageHolder?, data: T, position: Int, size: Int) -> Unit)? =null
    fun bind(listener: ((holder: BannerImageHolder?, data: T, position: Int, size: Int) -> Unit)){
        this.listener = listener
    }
    override fun onBindView(holder: BannerImageHolder?, data: T, position: Int, size: Int) {
        listener?.invoke(holder,data,position,size)
    }
}