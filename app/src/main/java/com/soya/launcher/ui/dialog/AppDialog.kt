package com.soya.launcher.ui.dialog

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.soya.launcher.R
import com.soya.launcher.enums.Atts
import com.soya.launcher.utils.AndroidSystem.isSystemApp
import com.soya.launcher.utils.AndroidSystem.uninstallPackage

class AppDialog : SingleDialogFragment(), View.OnClickListener {
    private var mCloseView: View? = null
    private var mDeleteView: View? = null
    private var mOpenView: View? = null

    private var mIV: ImageView? = null
    private var mNameView: TextView? = null
    private var mVersionView: TextView? = null
    private var mBlur: ImageView? = null
    private var mRootView: View? = null

    private var info: ApplicationInfo? = null

    private var callback: Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info = arguments!!.getParcelable(Atts.BEAN)
    }

    override fun getLayout(): Int {
        return R.layout.dialog_app
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOpenView!!.requestFocus()
    }

    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mCloseView = view.findViewById(R.id.close)
        mDeleteView = view.findViewById(R.id.delete)
        mOpenView = view.findViewById(R.id.open)
        mIV = view.findViewById(R.id.icon)
        mNameView = view.findViewById(R.id.name)
        mVersionView = view.findViewById(R.id.version)
        mBlur = view.findViewById(R.id.blur)
        mRootView = view.findViewById(R.id.root)

        blur(mRootView, mBlur)
    }

    override fun initBefore(inflater: LayoutInflater, view: View) {
        super.initBefore(inflater, view)
        mDeleteView!!.visibility =
            if (isSystemApp(info!!.flags)) View.GONE else View.VISIBLE
        mCloseView!!.setOnClickListener(this)
        mDeleteView!!.setOnClickListener(this)
        mOpenView!!.setOnClickListener(this)
    }

    override fun initBind(inflater: LayoutInflater, view: View) {
        super.initBind(inflater, view)
        try {
            val pm = activity!!.packageManager
            mIV!!.setImageDrawable(info!!.loadIcon(pm))
            mNameView!!.text = info!!.loadLabel(pm)
            mVersionView!!.text = pm.getPackageInfo(info!!.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }
    }

    override fun getWidthAndHeight(): IntArray {
        return intArrayOf(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
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
        if (v == mCloseView) {
            dismiss()
        } else if (v == mDeleteView) {
            //UninstallDialog.newInstance(info).show(getActivity().getSupportFragmentManager(), UninstallDialog.TAG);

            uninstallPackage(activity!!, info!!.packageName)
            dismiss()
        } else if (v == mOpenView) {
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
