package com.soya.launcher.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.soya.launcher.R
import com.soya.launcher.enums.Atts

class ToastDialog : SingleDialogFragment() {
    private var mTextView: TextView? = null
    private var mConfirmView: View? = null
    private var mCancelView: View? = null
    private var mRootView: View? = null
    private var mBlur: ImageView? = null

    private var callback: Callback? = null

    private var mode = MODE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments!!.getInt(Atts.MODE)
    }

    override fun getLayout(): Int {
        return R.layout.dialog_toast
    }

    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mTextView = view.findViewById(R.id.text)
        mConfirmView = view.findViewById(R.id.confirm)
        mRootView = view.findViewById(R.id.root)
        mBlur = view.findViewById(R.id.blur)
        mCancelView = view.findViewById(R.id.cancel)

        when (mode) {
            MODE_CONFIRM -> {
                mCancelView?.visibility = View.GONE
                mConfirmView?.visibility = View.VISIBLE
            }

            else -> {
                mCancelView?.visibility = View.VISIBLE
                mConfirmView?.visibility = View.VISIBLE
            }
        }
    }

    override fun initBind(inflater: LayoutInflater, view: View) {
        super.initBind(inflater, view)
        mTextView!!.text = arguments!!.getString(Atts.BEAN)
        mConfirmView!!.setOnClickListener {
            dismiss()
            if (callback != null) callback!!.onClick(1)
        }

        mCancelView!!.setOnClickListener {
            dismiss()
            if (callback != null) callback!!.onClick(0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blur(mRootView, mBlur)
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

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onClick(type: Int)
    }

    companion object {
        const val TAG: String = "ToastDialog"

        @JvmOverloads
        fun newInstance(text: String?, mode: Int = MODE_DEFAULT): ToastDialog {
            val args = Bundle()
            args.putString(Atts.BEAN, text)
            args.putInt(Atts.MODE, mode)
            val fragment = ToastDialog()
            fragment.arguments = args
            return fragment
        }

        const val MODE_DEFAULT: Int = 0
        const val MODE_CONFIRM: Int = 1
    }
}
