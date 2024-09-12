package com.shudong.lib_base.base

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/7 14:48
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.base
 */
interface BaseVMView {
    fun initView() {}
    fun initBeforeContent() {}
    fun initBeforeBaseLayout() {}
    fun initdata() {}
    fun initClick() {}
    fun initObserver() {}

    fun handleSoftInput() {}
    fun closeSoftInput() {}

    fun loadingView(msg: String = "")
    fun hideLoading()
}