package com.soya.launcher.pop

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

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
