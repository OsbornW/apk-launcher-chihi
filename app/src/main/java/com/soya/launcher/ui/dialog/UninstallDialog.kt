package com.soya.launcher.ui.dialog

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.soya.launcher.R
import com.soya.launcher.enums.Atts

class UninstallDialog : SingleDialogFragment(), View.OnClickListener {
    private var mOptView: TextView? = null
    private var mMsgView: TextView? = null
    private var mBlur: ImageView? = null
    private var mRootView: View? = null

    private var info: ApplicationInfo? = null
    private var isSuccess: Boolean? = null

    //private InnerBroadcast mBroadcast;
    private var isEnd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info = arguments!!.getParcelable(Atts.BEAN)
        isSuccess = arguments!!.getBoolean("issuccess")
        //mBroadcast = new InnerBroadcast();
        //IntentFilter filter = new IntentFilter();
        //filter.addAction(IntentAction.ACTION_DELETE_PACKAGE);
        //getActivity().registerReceiver(mBroadcast, filter);
    }

    override fun getLayout(): Int {
        return R.layout.dialog_uninstall
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOptView!!.requestFocus()
        //blur(mRootView, mBlur);
    }

    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mOptView = view.findViewById(R.id.opt)
        mMsgView = view.findViewById(R.id.msg)
        mBlur = view.findViewById(R.id.blur)
        mRootView = view.findViewById(R.id.root)
        blur(mRootView, mBlur)
    }

    override fun initBefore(inflater: LayoutInflater, view: View) {
        super.initBefore(inflater, view)
        mOptView!!.setOnClickListener(this)
    }

    override fun initBind(inflater: LayoutInflater, view: View) {
        super.initBind(inflater, view)
        if (isSuccess!!) {
            mMsgView!!.text = getString(R.string.uninstalled)
        } else {
            mMsgView!!.text = getString(R.string.uninstall_failed)
        }
        mOptView!!.text = getString(R.string.close)
        isEnd = true
        //mMsgView.setText(getString(R.string.uninstalling));
        //AndroidSystem.uninstallPackage(getActivity(), info.packageName);
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
        if (v == mOptView) {
            if (isEnd) dismiss()
        }
    } /*    public final class InnerBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ;
            int code = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1);
            boolean success = code == PackageInstaller.STATUS_SUCCESS;
            mMsgView.setText(getString(success ? R.string.uninstalled : R.string.uninstall_failed));
            mOptView.setText(getString(R.string.close));
            isEnd = true;
        }
    }*/

    companion object {
        const val TAG: String = "UninstallDialog"
        fun newInstance(info: ApplicationInfo?, isSuccess: Boolean?): UninstallDialog {
            val args = Bundle()
            args.putParcelable(Atts.BEAN, info)
            args.putBoolean("issuccess", isSuccess!!)
            val fragment = UninstallDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
