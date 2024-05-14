package com.shudong.lib_base.toast.config;

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   Toast拦截器接口
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast.config
 */
public interface IToastInterceptor {

    /**
     * 根据显示的文本决定是否拦截该 Toast
     */
    boolean intercept(CharSequence text);
}