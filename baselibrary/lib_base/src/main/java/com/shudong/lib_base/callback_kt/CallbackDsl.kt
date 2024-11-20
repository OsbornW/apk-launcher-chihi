package com.thumbsupec.lib_base.cllback_kt


/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/31 11:25
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.cllback_kt
 */

fun span(callback: SpanClick.() -> Unit):SpanClick{
    val callBack = object :SpanClick(){}
    callBack.callback()
    return callBack
}



fun viewStatus(callback: GlobalLayout.() -> Unit):GlobalLayout{
    val callBack = object :GlobalLayout(){}
    callBack.callback()
    return callBack
}

/*fun <T> bannerAdapter(list:MutableList<T>, callback: BannerAdapter<T>.() -> Unit):BannerAdapter<T>{
    val callBack = object : BannerAdapter<T>(list){}
    callBack.callback()
    return callBack
}*/
