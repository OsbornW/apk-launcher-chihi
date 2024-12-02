package com.soya.launcher.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.databinding.DialogWifiSaveBinding
import com.soya.launcher.enums.Atts

class WifiSaveDialog : SingleDialogFragment<DialogWifiSaveBinding>() {

    private var callback: Callback? = null

    val textTitleData = MutableLiveData<String>()
    val textContentData = MutableLiveData<String>()
    val textCloseData = MutableLiveData<String>()
    val textCleanData = MutableLiveData<String>()
    val textConfirmData = MutableLiveData<String>()

    override fun init( view: View) {

        textTitleData.observe(this){
            binding.tvTitle.text = it
        }
        textContentData.observe(this){
            binding.wifiName.text = it
        }

        textCloseData.observe(this){
            binding.close.text = it
        }

        textCleanData.observe(this){
            binding.clean.text = it
        }

        textConfirmData.observe(this){
            binding.confirm.text = it
        }

        binding.close.clickNoRepeat {
            closeAction?.invoke()
            dismiss()
        }
        binding.clean.clickNoRepeat {
            cleanAction?.invoke()
            dismiss()
        }
        binding.confirm.clickNoRepeat {
            confirmAction?.invoke()
            dismiss()
        }

        blur(binding.root, binding.blur)

    }

    private var closeAction: (() -> Unit)? = null  //
    fun closeAction(action: () -> Unit) {
        closeAction = action
    }

    private var cleanAction: (() -> Unit)? = null  //
    fun cleanAction(action: () -> Unit) {
        cleanAction = action
    }

    private var confirmAction: (() -> Unit)? = null  //
    fun confirmAction(action: () -> Unit) {
        confirmAction = action
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.confirm.requestFocus()

    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
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
