package com.chihihx.launcher.ui.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.ext.clickNoRepeat
import com.chihihx.launcher.databinding.DialogWifiPassBinding
import com.chihihx.launcher.pop.showKeyBoardDialog
import kotlinx.coroutines.launch

class WifiPassDialog : SingleDialogFragment<DialogWifiPassBinding>() {

    var wifiName = ""

    val textTitleData = MutableLiveData<String>()
    val textWifiNameData = MutableLiveData<String>()
    override fun init(view: View) {

        textTitleData.observe(this){
            binding.tvTitle.text = it
        }
        textWifiNameData.observe(this){
            binding.editPass.setText(
                when(wifiName){
                    "WIFI-5G","WIFI"->"chen888888a"
                    "wuyun-5G","wuyun"->"Boss888888"
                    else->""
                }
            )
        }




        binding.editPass.apply {
            onConfirmClick = {
                showKeyBoardDialog {
                    editTextData.value = binding.editPass
                }
            }

        }

        binding.close.clickNoRepeat {
            closeAction?.invoke()
            dismiss()
        }
        binding.clean.clickNoRepeat {
            cleanAction?.invoke()
            binding.editPass.setText("")
            dismiss()
        }
        binding.confirm.clickNoRepeat {
            val pwd = binding.editPass.text.toString()
            if (!TextUtils.isEmpty(pwd)) {
                confirmAction?.invoke(pwd)
                dismiss()
            }

        }



        lifecycleScope.launch {
            //delay(600)
            showKeyBoardDialog {
                editTextData.value = binding.editPass
            }
        }

    }

    private var closeAction: (() -> Unit)? = null  //
    fun closeAction(action: () -> Unit) {
        closeAction = action
    }

    private var cleanAction: (() -> Unit)? = null  //
    fun cleanAction(action: () -> Unit) {
        cleanAction = action
    }

    private var confirmAction: ((pwd:String) -> Unit)? = null  //
    fun confirmAction(action: (pwd:String) -> Unit) {
        confirmAction = action
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


    fun setRootGravity(gravity: Int) {
        val lp = binding.root.layoutParams as FrameLayout.LayoutParams
        lp.gravity = gravity
        binding.root.layoutParams = lp
        blur(binding.root, binding.blur)
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
