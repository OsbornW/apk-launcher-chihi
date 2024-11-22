package com.soya.launcher.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.ViewGroup
import com.soya.launcher.R
import com.soya.launcher.databinding.DialogProgressBinding

class ProgressDialog : SingleDialogFragment<DialogProgressBinding>() {
    private var callback: Callback? = null



    override fun canOutSide(): Boolean {
        return false
    }

    override fun isMaterial(): Boolean {
        return false
    }

    override fun getWidthAndHeight(): Pair<Int,Int> {
        return ViewGroup.LayoutParams.MATCH_PARENT to ViewGroup.LayoutParams.MATCH_PARENT
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
