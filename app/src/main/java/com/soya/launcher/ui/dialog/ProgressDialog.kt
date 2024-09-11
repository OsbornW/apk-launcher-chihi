package com.soya.launcher.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.ViewGroup
import com.soya.launcher.R

class ProgressDialog : SingleDialogFragment() {
    private var callback: Callback? = null

    override fun getLayout(): Int {
        return R.layout.dialog_progress
    }

    override fun canOutSide(): Boolean {
        return false
    }

    override fun isMaterial(): Boolean {
        return false
    }

    override fun getWidthAndHeight(): IntArray {
        return intArrayOf(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (callback != null) callback!!.onDismiss()
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onDismiss()
    }

    companion object {
        const val TAG: String = "ProgressDialog"

        fun newInstance(): ProgressDialog {
            val args = Bundle()

            val fragment = ProgressDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
