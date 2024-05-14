package com.shudong.lib_base.toast.config;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   默认样式接口
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast.config
 */
public interface IToastStyle<V extends View> {

    /**
     * 创建 Toast 视图
     */
    V createView(Context context);

    /**
     * 获取 Toast 显示重心
     */
    default int getGravity() {
        return Gravity.CENTER;
    }

    /**
     * 获取 Toast 水平偏移
     */
    default int getXOffset() {
        return 0;
    }

    /**
     * 获取 Toast 垂直偏移
     */
    default int getYOffset() {
        return 0;
    }

    /**
     * 获取 Toast 水平间距
     */
    default float getHorizontalMargin() {
        return 0;
    }

    /**
     * 获取 Toast 垂直间距
     */
    default float getVerticalMargin() {
        return 0;
    }
}