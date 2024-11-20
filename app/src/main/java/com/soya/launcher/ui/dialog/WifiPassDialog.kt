package com.soya.launcher.ui.dialog

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.soya.launcher.R
import com.soya.launcher.pop.showKeyBoardDialog
import com.soya.launcher.view.CustomEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WifiPassDialog : SingleDialogFragment(), View.OnClickListener {
    var wifiName = ""
    private var mEditText: CustomEditText? = null
    private var mCloseView: View? = null
    private var mCleanView: View? = null
    private var mConfirmView: View? = null
    private var mRootView: View? = null
    private var mBlur: ImageView? = null
    private var callback: Callback? = null
    override fun getLayout(): Int {
        return R.layout.dialog_wifi_pass
    }

    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mEditText = view.findViewById(R.id.edit_pass)
        mCleanView = view.findViewById(R.id.clean)
        mCloseView = view.findViewById(R.id.close)
        mConfirmView = view.findViewById(R.id.confirm)
        mRootView = view.findViewById(R.id.root)
        mBlur = view.findViewById(R.id.blur)
        wifiName = arguments?.getString("wifiname").toString()

        if (wifiName == "WIFI-5G") {
            mEditText!!.setText("chen888888a")
        } else if (wifiName == "WIFI") {
            mEditText!!.setText("chen888888a")
        } else if (wifiName == "wuyun") {
            mEditText!!.setText("Boss888888")
        } else if (wifiName == "wuyun-5G") {
            mEditText!!.setText("Boss888888")
        }

        mEditText?.apply {
            onConfirmClick = {
                showKeyBoardDialog {
                    editTextData.value = mEditText
                }
            }

        }

        lifecycleScope.launch {
            delay(600)
            showKeyBoardDialog {
                editTextData.value = mEditText
            }
        }

    }

    override fun initBefore(inflater: LayoutInflater, view: View) {
        super.initBefore(inflater, view)
        mCleanView!!.setOnClickListener(this)
        mCloseView!!.setOnClickListener(this)
        mConfirmView!!.setOnClickListener(this)
        mEditText!!.setOnClickListener(this)
        mEditText!!.setOnEditorActionListener { textView, i, keyEvent ->
            val text = textView.text.toString()
            setRootGravity(Gravity.CENTER)
            if (callback != null) callback!!.onConfirm(text)
            dismiss()
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEditText!!.requestFocus()
        blur(mRootView, mBlur)
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    override fun isMaterial(): Boolean {
        return false
    }

    override fun getWidthAndHeight(): IntArray {
        return intArrayOf(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun getDimAmount(): Float {
        return 0f
    }

    override fun onClick(v: View) {
        if (v == mCloseView) {
            dismiss()
        } else if (v == mCleanView) {
            mEditText!!.setText("")
        } else if (v == mConfirmView) {
            val text = mEditText!!.text.toString()
            if (callback != null && !TextUtils.isEmpty(text)) {
                callback!!.onConfirm(text)
                dismiss()
            }
        }
    }

    fun setRootGravity(gravity: Int) {
        val lp = mRootView!!.layoutParams as FrameLayout.LayoutParams
        lp.gravity = gravity
        mRootView!!.layoutParams = lp
        blur(mRootView, mBlur)
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun setDefaultPwd(wifiName: String?) {
        if (wifiName != null && mEditText != null) {
            if (wifiName == "WIFI-5G") {
                mEditText!!.setText("chen888888a")
            } else if (wifiName == "WIFI") {
                mEditText!!.setText("chen888888a")
            } else if (wifiName == "wuyun") {
                mEditText!!.setText("Boss888888")
            } else if (wifiName == "wuyun-5G") {
                mEditText!!.setText("Boss888888")
            }
        }
    }

    interface Callback {
        fun onConfirm(text: String)
    }

    companion object {
        const val TAG = "WifiPassDialog"
        fun newInstance(wifiName: String?): WifiPassDialog {
            val args = Bundle()
            args.putString("wifiname",wifiName)
            val fragment = WifiPassDialog()
            fragment.setArguments(args)
            return fragment
        }
    }
}
