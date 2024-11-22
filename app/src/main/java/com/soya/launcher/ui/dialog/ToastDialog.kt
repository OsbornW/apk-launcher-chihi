package com.soya.launcher.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.soya.launcher.R
import com.soya.launcher.databinding.DialogToastBinding
import com.soya.launcher.enums.Atts

class ToastDialog : SingleDialogFragment<DialogToastBinding>() {


    private var callback: Callback? = null

    private var mode = MODE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments!!.getInt(Atts.MODE)
    }



    override fun init( view: View) {


        when (mode) {
            MODE_CONFIRM -> {
                binding.cancel.visibility = View.GONE
                binding.confirm.visibility = View.VISIBLE
            }

            else -> {
                binding.cancel.visibility = View.VISIBLE
                binding.confirm.visibility = View.VISIBLE
            }
        }
    }

    override fun initBind(view: View) {
        binding.text.text = arguments!!.getString(Atts.BEAN)
        binding.confirm.setOnClickListener {
            dismiss()
            if (callback != null) callback!!.onClick(1)
        }

        binding.cancel.setOnClickListener {
            dismiss()
            if (callback != null) callback!!.onClick(0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blur(binding.root, binding.blur)
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
