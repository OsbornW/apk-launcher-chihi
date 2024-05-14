package com.soya.launcher.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shudong.lib_base.ext.d
import com.shudong.lib_base.pop.CenterCommonDialog.Companion.dialog
import com.soya.launcher.R
import com.soya.launcher.adapter.KeyboardAdapter
import com.soya.launcher.bean.KeyItem

class CusKeyboard private constructor(val setup: (CusKeyboard.() -> Unit)? = null) :
    SingleDialogFragment(), KeyboardAdapter.Callback {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: KeyboardAdapter? = null
    private var callback: Callback? = null
    private val ens: MutableList<KeyItem> = ArrayList()
    private val nums: MutableList<KeyItem> = ArrayList()
    private var mTargetView: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setup?.invoke(this)
        super.onCreate(savedInstanceState)
        fillEn()
        fillNums()
    }

    override fun getLayout(): Int {
        return R.layout.dialog_keyboard
    }

    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mRecyclerView = view.findViewById(R.id.recycler)
        mAdapter = KeyboardAdapter(activity, inflater, ArrayList())
    }

    override fun initBefore(inflater: LayoutInflater, view: View) {
        super.initBefore(inflater, view)
        mAdapter!!.setCallback(this)
        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                if (event.unicodeChar == 0) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DEL -> del()
                    }
                } else {
                    mTargetView!!.append(event.displayLabel.toString())
                }
            }
            false
        }
    }

    override fun initBind(inflater: LayoutInflater, view: View) {
        super.initBind(inflater, view)
        mAdapter!!.replace(ens, KeyboardAdapter.TYPE_ENG)
        mRecyclerView!!.setLayoutManager(GridLayoutManager(activity, 10))
        mRecyclerView!!.setAdapter(mAdapter)
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    }

    override fun getWidthAndHeight(): IntArray {
        return intArrayOf(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )
    }

    override fun getDimAmount(): Float {
        return 0f
    }

    fun setTargetView(targetView: EditText?) {
        mTargetView = targetView
    }

    private fun fillEn() {
        var array =
            "q w e r t y u i o p".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        Log.e(TAG, "fillEn: " + array.contentToString())
        for (item in array) ens.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        array =
            "a s d f g h j k l ,".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) ens.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        ens.add(KeyItem(KeyItem.TYPE_UPCAST, "", R.drawable.baseline_lower_cast_100, 1, true))
        array = "z x c v b n m .".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) ens.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        ens.add(KeyItem(KeyItem.TYPE_DEL, "", R.drawable.baseline_backspace_100, 1, true))
        ens.add(KeyItem(KeyItem.TYPE_SWITCH, "?123", -1, 2, false))
        ens.add(KeyItem(KeyItem.TYPE_SPACE, " ", -1, 6, false))
        ens.add(KeyItem(KeyItem.TYPE_SEARCH, "DONE", -1, 2, true))
    }

    private fun fillNums() {
        var array =
            "1 2 3 4 5 6 7 8 9 0".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) nums.add(KeyItem(KeyItem.TYPE_NUM, item, -1, 1, false))
        array =
            "! @ # $ % ^ & * ( )".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) nums.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        array =
            "~ , . ? / \" - + =".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in array) nums.add(KeyItem(KeyItem.TYPE_ENG, item, -1, 1, false))
        nums.add(KeyItem(KeyItem.TYPE_DEL, "", R.drawable.baseline_backspace_100, 1, true))
        nums.add(KeyItem(KeyItem.TYPE_SWITCH, "ABC", -1, 2, false))
        nums.add(KeyItem(KeyItem.TYPE_SPACE, " ", -1, 6, false))
        nums.add(KeyItem(KeyItem.TYPE_SEARCH, "DONE", -1, 2, true))
    }

    override fun onClick(bean: KeyItem, text: String) {
        when (bean.type) {
            KeyItem.TYPE_SWITCH -> if (mAdapter!!.type == KeyboardAdapter.TYPE_ENG) {
                mAdapter!!.replace(nums, KeyboardAdapter.TYPE_NUM)
            } else if (mAdapter!!.type == KeyboardAdapter.TYPE_NUM) {
                mAdapter!!.replace(ens, KeyboardAdapter.TYPE_ENG)
            }

            KeyItem.TYPE_UPCAST -> mAdapter!!.setUPCase(!mAdapter!!.isUPCase)
            KeyItem.TYPE_DEL -> del()
            KeyItem.TYPE_SEARCH -> {
                mTargetView!!.onEditorAction(EditorInfo.IME_ACTION_DONE)
                dismiss()
                inputCompleted?.invoke()
            }

            else -> mTargetView!!.append(text)
        }
    }

    private fun del() {
        val len = mTargetView!!.text.length
        if (len != 0) mTargetView?.setText(mTargetView?.text?.subSequence(0, len - 1))
        mTargetView?.setSelection(mTargetView?.text?.toString()?.length?:0)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (callback != null) callback!!.onClose()
        keboardHide?.invoke()
    }

    private var keboardHide: (() -> Unit)? = null

    /**
     * 键盘隐藏
     */
    fun keboardHide(listener: (() -> Unit)?) {
        keboardHide = listener
    }

    private var inputCompleted: (() -> Unit)? = null

    /**
     * 键盘隐藏
     */
    fun inputCompleted(listener: (() -> Unit)?) {
        inputCompleted = listener
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onClose()
    }


    companion object {
        const val TAG = "KeyboardDialog"
        var fragment: CusKeyboard? = null
        var args: Bundle? = null

        @JvmStatic
        fun newInstance(setup: (CusKeyboard.() -> Unit)? = null): CusKeyboard {
            fragment?.dismiss()
            fragment = null
            args = null
            args = Bundle()
            fragment = CusKeyboard(setup)
            fragment?.setArguments(args)
            return fragment!!
        }
    }
}
