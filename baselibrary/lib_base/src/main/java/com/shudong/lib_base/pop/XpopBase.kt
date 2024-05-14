package com.shudong.lib_base.pop

import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.LoadingPopupView
import com.shudong.lib_base.R
import com.shudong.lib_base.currentActivity

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/06 22:20
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.pop
 */

fun <T : BasePopupView> getPop(
    popView: BasePopupView? = null,
    isLoading: Boolean = false,
    build: (XPopup.Builder.() -> Unit)? = null
): T {
    val builder = XPopup.Builder(currentActivity)
        .dismissOnBackPressed(true)
        .isViewMode(true)
        .animationDuration(500)
        .hasShadowBg(true)
    build?.invoke(builder)
    if (isLoading) {
        val pop =
            builder.asLoading(null, R.layout.layout_loading, LoadingPopupView.Style.ProgressBar)
                .show()
        return pop as T
    } else {
        val pop = builder.asCustom(popView).show()
        return pop as T
    }

}
