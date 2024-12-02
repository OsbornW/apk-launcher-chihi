package com.soya.launcher.pop

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.soya.launcher.ui.dialog.WifiPassDialog
import com.soya.launcher.ui.dialog.WifiSaveDialog

/*fun showTestDialog(setup: (TestDialog.() -> Unit)? = null,build: (XPopup.Builder.() -> Unit)? = null) =
    getPop<TestDialog>(currentActivity?.let {
        TestDialog.newInstance(it) {
            setup?.invoke(
                this
            )
        }
    }){
        build?.invoke(this)
        //dismissOnBackPressed(false)
    }*/


fun FragmentActivity.showKeyBoardDialog(builderActions: CusKeyBoardFragment.() -> Unit) {
    CusKeyBoardFragment().apply(builderActions).show(supportFragmentManager,"CusKeyBoardFragment")
}

fun Fragment.showKeyBoardDialog(builderActions: CusKeyBoardFragment.() -> Unit) {
    CusKeyBoardFragment().apply(builderActions).show(childFragmentManager,"CusKeyBoardFragment")
}

fun Fragment.showWifiSavedDialog(builderActions: WifiSaveDialog.() -> Unit) {
    WifiSaveDialog().apply(builderActions).show(childFragmentManager,"WifiSaveDialog")
}

fun Fragment.showWifiPWDDialog(builderActions: WifiPassDialog.() -> Unit) {
    WifiPassDialog().apply(builderActions).show(childFragmentManager,"WifiPassDialog")
}
