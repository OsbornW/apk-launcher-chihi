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
import com.soya.launcher.databinding.DialogWifiSaveBinding
import com.soya.launcher.enums.Atts

class WifiSaveDialog : SingleDialogFragment<DialogWifiSaveBinding>(), View.OnClickListener {

    private var callback: Callback? = null


    override fun init( view: View) {

        binding.wifiName.text = arguments!!.getString(Atts.BEAN)
        (arguments!!.getString("oktext")).toString().isNotEmpty().yes {
            binding.confirm.text = arguments!!.getString("oktext")
        }

        blur(binding.root, binding.blur)

    }


    override fun initBefore( view: View) {
        binding.clean.setOnClickListener(this)
        binding.close.setOnClickListener(this)
        binding.confirm.setOnClickListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.confirm.requestFocus()

    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    override fun onClick(v: View) {
        if (v == binding.close) {
            dismiss()
        } else if (v == binding.clean) {
            if (callback != null) {
                callback!!.onClick(-1)
                dismiss()
            }
        } else if (v == binding.confirm) {
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

    override fun getWidthAndHeight(): Pair<Int,Int> {
        return ViewGroup.LayoutParams.MATCH_PARENT to ViewGroup.LayoutParams.MATCH_PARENT
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
