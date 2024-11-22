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
import com.blankj.utilcode.util.SnackbarUtils.dismiss
import com.soya.launcher.R
import com.soya.launcher.databinding.DialogWifiPassBinding
import com.soya.launcher.pop.showKeyBoardDialog
import com.soya.launcher.view.CustomEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WifiPassDialog : SingleDialogFragment<DialogWifiPassBinding>(), View.OnClickListener {
    var wifiName = ""
   
    private var callback: Callback? = null


    override fun init(view: View) {
       
        wifiName = arguments?.getString("wifiname").toString()

        if (wifiName == "WIFI-5G") {
            binding.editPass.setText("chen888888a")
        } else if (wifiName == "WIFI") {
            binding.editPass.setText("chen888888a")
        } else if (wifiName == "wuyun") {
            binding.editPass.setText("Boss888888")
        } else if (wifiName == "wuyun-5G") {
            binding.editPass.setText("Boss888888")
        }

        binding.editPass.apply {
            onConfirmClick = {
                showKeyBoardDialog {
                    editTextData.value = binding.editPass
                }
            }

        }

        lifecycleScope.launch {
            delay(600)
            showKeyBoardDialog {
                editTextData.value = binding.editPass
            }
        }

    }

    override fun initBefore( view: View) {
        binding.clean.setOnClickListener(this)
        binding.close.setOnClickListener(this)
        binding.confirm.setOnClickListener(this)
        binding.editPass.setOnClickListener(this)
        binding.editPass.setOnEditorActionListener { textView, i, keyEvent ->
            val text = textView.text.toString()
            setRootGravity(Gravity.CENTER)
            if (callback != null) callback!!.onConfirm(text)
            dismiss()
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editPass.requestFocus()
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

    override fun onClick(v: View) {
        if (v == binding.close) {
            dismiss()
        } else if (v == binding.clean) {
            binding.editPass.setText("")
        } else if (v == binding.confirm) {
            val text = binding.editPass.text.toString()
            if (callback != null && !TextUtils.isEmpty(text)) {
                callback!!.onConfirm(text)
                dismiss()
            }
        }
    }

    fun setRootGravity(gravity: Int) {
        val lp = binding.root.layoutParams as FrameLayout.LayoutParams
        lp.gravity = gravity
        binding.root.layoutParams = lp
        blur(binding.root, binding.blur)
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun setDefaultPwd(wifiName: String?) {
        if (wifiName != null && binding.editPass != null) {
            if (wifiName == "WIFI-5G") {
                binding.editPass.setText("chen888888a")
            } else if (wifiName == "WIFI") {
                binding.editPass.setText("chen888888a")
            } else if (wifiName == "wuyun") {
                binding.editPass.setText("Boss888888")
            } else if (wifiName == "wuyun-5G") {
                binding.editPass.setText("Boss888888")
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
