package com.shudong.lib_base.toast.config;

import android.app.Application;

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   Toast处理策略
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast.config
 */
public interface IToastStrategy {

    /**
     * 注册策略
     */
    void registerStrategy(Application application);

    /**
     * 绑定样式
     */
    void bindStyle(IToastStyle<?> style);

    /**
     * 创建 Toast
     */
    IToast createToast(Application application);

    /**
     * 显示 Toast
     */
    void showToast(CharSequence text, long delayMillis);

    /**
     * 取消 Toast
     */
    void cancelToast();
}