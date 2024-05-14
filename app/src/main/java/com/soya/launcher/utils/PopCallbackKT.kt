package com.soya.launcher.utils

import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.XPopupCallback

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2023/2/16 17:06
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.cllback_kt
 */
open class PopCallbackKT :XPopupCallback{
    override fun onCreated(popupView: BasePopupView?) {
        // 弹窗内部onCreate执行完调用
    }

    override fun beforeShow(popupView: BasePopupView?) {
        // 在每次show之前都会执行，可以用来进行多次的数据更新
    }

    override fun onShow(popupView: BasePopupView?) {
        // 完全显示的时候执行
    }

    override fun onDismiss(popupView: BasePopupView?) {
        // 完全隐藏的时候执行
    }

    override fun beforeDismiss(popupView: BasePopupView?) {
        // 完全隐藏之前候执行
    }

    override fun onBackPressed(popupView: BasePopupView?): Boolean {
        // 如果想拦截返回按键事件，则重写这个方法，返回true即可
        return false
    }

    override fun onKeyBoardStateChanged(popupView: BasePopupView?, height: Int) {
        // 监听软键盘高度变化，高度为0说明软键盘关闭，反之则打开
    }

    override fun onDrag(popupView: BasePopupView?, value: Int, percent: Float, upOrLeft: Boolean) {
        // 监听弹窗拖拽，适用于能拖拽的弹窗
    }

    override fun onClickOutside(popupView: BasePopupView?) {
        // 点击了窗体外
    }
}