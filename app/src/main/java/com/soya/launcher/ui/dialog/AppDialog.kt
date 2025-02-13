package com.soya.launcher.ui.dialog

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.yes
import com.soya.launcher.databinding.DialogAppBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.ext.uninstallApkForNormalApp
import com.soya.launcher.ext.uninstallApp
import com.soya.launcher.utils.AndroidSystem.isSystemApp

class AppDialog : SingleDialogFragment<DialogAppBinding>(), View.OnClickListener {


    private var info: ApplicationInfo? = null

    private var callback: Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info = requireArguments().getParcelable(Atts.BEAN)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.open.requestFocus()
    }

    override fun init(view: View) {

        blur(binding.root, binding.blur)
    }

    override fun initBefore(view: View) {
        binding.delete.visibility =
            if (isSystemApp(info!!.flags)) View.GONE else View.VISIBLE
        binding.close.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
        binding.open.setOnClickListener(this)
    }

    override fun initBind( view: View) {
        try {
            val pm = requireActivity().packageManager
            binding.icon.setImageDrawable(info!!.loadIcon(pm))
            binding.name.text = info!!.loadLabel(pm)
            binding.version.text = pm.getPackageInfo(info!!.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }
    }

    override fun getWidthAndHeight(): Pair<Int,Int> {
        return ViewGroup.LayoutParams.MATCH_PARENT to ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun getDimAmount(): Float {
        return 0f
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    override fun isMaterial(): Boolean {
        return false
    }

    override fun onClick(v: View) {
        if (v == binding.close) {
            dismiss()
        } else if (v == binding.delete) {
            //UninstallDialog.newInstance(info).show(getActivity().getSupportFragmentManager(), UninstallDialog.TAG);

           // uninstallPackage(requireActivity(), info!!.packageName)
            /*info?.packageName?.let {
                it.uninstallApp{isUninstallSuccess->
                    isUninstallSuccess.yes {
                    }
                }
            }*/
            "开始进行卸载操作：${info?.packageName}".e("chihi_error")
            info?.packageName?.uninstallApkForNormalApp()

            dismiss()
        } else if (v == binding.open) {
            if (callback != null) callback!!.onOpen()
            dismiss()
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onOpen()
    }

    companion object {
        const val TAG: String = "AppDialog"
        fun newInstance(info: ApplicationInfo?): AppDialog {
            val args = Bundle()
            args.putParcelable(Atts.BEAN, info)
            val fragment = AppDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
