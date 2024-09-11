package com.soya.launcher.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager

open class TranslucentDialog( val mContext: Context, protected var layoutId: Int) : Dialog(
    mContext
) {
    protected var inflater: LayoutInflater = LayoutInflater.from(mContext)
    protected var decorView: View? = null
    protected var uiHandler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(layoutId)

        val window = window
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (isTorch) {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                        or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                        or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG
            )
        }

        window.setDimAmount(dimAmount)
        if (animation != -1) window.setWindowAnimations(animation)

        val lp = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        lp.width = width
        lp.height = height
        window.attributes = lp
        getWindow()!!.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        decorView = window.decorView
    }

    protected open val isTorch: Boolean
        get() = true

    protected val animation: Int
        get() = -1

    protected val width: Int
        get() = WindowManager.LayoutParams.MATCH_PARENT

    protected val height: Int
        get() = WindowManager.LayoutParams.MATCH_PARENT

    protected open val dimAmount: Float
        get() = 0.5f
}
