package com.shudong.lib_base.ext.loading

import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.LoadingPopupView
import com.shudong.lib_base.ext.globalCoroutine
import com.shudong.lib_base.ext.isRepeatExcute
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.pop.getPop
import kotlinx.coroutines.launch

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/27 16:29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

var loadingPopup: LoadingPopupView? = null
fun showLoadingView(title:String = "") {
    globalCoroutine().launch {
        (loadingPopup ==null).yes {
            isRepeatExcute(500).no {
                showLoadingViewDismiss()
                loadingPopup = getPop(isLoading = true){
                    hasShadowBg(false)
                    dismissOnTouchOutside(false)
                    dismissOnBackPressed(true)
                    setPopupCallback(object : PopCallbackKT() {
                        override fun onShow(popupView: BasePopupView?) {
                            //true.invokeLoadingStatus()
                        }

                        override fun onDismiss(popupView: BasePopupView?) {
                            //false.invokeLoadingStatus()
                        }
                    })
                }
            }

        }
        loadingPopup?.setTitle(title)
    }

}

fun showLoadingView(title:String = "",isCanBack:Boolean = true) {
    globalCoroutine().launch {
        (loadingPopup ==null).yes {
                showLoadingViewDismiss()
                loadingPopup = getPop(isLoading = true){
                    hasShadowBg(false)
                    dismissOnTouchOutside(false)
                    dismissOnBackPressed(isCanBack)
                    setPopupCallback(object : PopCallbackKT() {
                        override fun onShow(popupView: BasePopupView?) {
                            //true.invokeLoadingStatus()
                        }

                        override fun onDismiss(popupView: BasePopupView?) {
                            //false.invokeLoadingStatus()
                        }
                    })
                }


        }
        loadingPopup?.setTitle(title)
    }

}

fun showLoadingViewDismiss() {
    loadingPopup?.dismiss()
    loadingPopup = null
}