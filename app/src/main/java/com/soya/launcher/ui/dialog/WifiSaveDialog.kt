package com.soya.launcher.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.enums.Atts

class WifiSaveDialog : SingleDialogFragment(), View.OnClickListener {
    private var mCloseView: View? = null
    private var mCleanView: View? = null
    private var mConfirmView: View? = null
    private var mNameView: TextView? = null
    private var mRootView: View? = null
    private var mBlur: ImageView? = null
    private var callback: Callback? = null
    override fun getLayout(): Int {
        return R.layout.dialog_wifi_save
    }

    var okText = ""
    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mCleanView = view.findViewById(R.id.clean)
        mCloseView = view.findViewById(R.id.close)
        mConfirmView = view.findViewById(R.id.confirm)
        mNameView = view.findViewById(R.id.wifi_name)
        mRootView = view.findViewById(R.id.root)
        mBlur = view.findViewById(R.id.blur)
        mNameView?.text = arguments!!.getString(Atts.BEAN)
        (arguments!!.getString("oktext")).toString().isNotEmpty().yes {
            (mConfirmView as TextView).setText(arguments!!.getString("oktext"))
        }

        blur(mRootView, mBlur)

    }


    override fun initBefore(inflater: LayoutInflater, view: View) {
        super.initBefore(inflater, view)
        mCleanView!!.setOnClickListener(this)
        mCloseView!!.setOnClickListener(this)
        mConfirmView!!.setOnClickListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mConfirmView!!.requestFocus()

    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    override fun onClick(v: View) {
        if (v == mCloseView) {
            dismiss()
        } else if (v == mCleanView) {
            if (callback != null) {
                callback!!.onClick(-1)
                dismiss()
            }
        } else if (v == mConfirmView) {
            if (callback != null) {
                callback!!.onClick(0)
                dismiss()
            }
        }
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    override fun isMaterial(): Boolean {
        return false
    }

    override fun getWidthAndHeight(): IntArray {
        return intArrayOf(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun getDimAmount(): Float {
        return 0f
    }

    interface Callback {
        fun onClick(type: Int)
    }

    companion object {
        const val TAG = "WifiSaveDialog"
        fun newInstance(name: String?,okText:String): WifiSaveDialog {
            val args = Bundle()
            args.putString(Atts.BEAN, name)
            args.putString("oktext", okText)
            val fragment = WifiSaveDialog()
            fragment.setArguments(args)
            return fragment
        }
    }
}
