package com.soya.launcher.ui.dialog

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.soya.launcher.R

class RemoteDialog( mContext: Context, layoutId: Int) : TranslucentDialog(mContext, layoutId) {

    private var mNameView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        mNameView = decorView?.findViewById(R.id.blu_name)
    }

    override val isTorch: Boolean
        get() = false


    override val dimAmount: Float
        get() = 0f


    fun setName(name: String?) {
        if (mNameView != null) mNameView!!.text = name
    }
}
