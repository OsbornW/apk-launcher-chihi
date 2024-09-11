package com.soya.launcher.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.HorizontalGridView
import com.soya.launcher.R
import com.soya.launcher.adapter.DateSelectAdapter
import java.util.Calendar

class TimePickerDialog : SingleDialogFragment(), View.OnClickListener {
    private val calendar: Calendar = Calendar.getInstance()

    private var mHourGrid: HorizontalGridView? = null
    private var mMinthGrid: HorizontalGridView? = null
    private var mSencondGrid: HorizontalGridView? = null
    private var mCancelView: View? = null
    private var mConfirmView: View? = null
    private var mBlur: ImageView? = null
    private var mRootView: View? = null

    private var mHourAdapter: DateSelectAdapter? = null
    private var mMinuteAdapter: DateSelectAdapter? = null
    private var mSecondAdapter: DateSelectAdapter? = null

    private var callback: Callback? = null

    override fun getLayout(): Int {
        return R.layout.dialog_time_picker
    }

    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mHourGrid = view.findViewById(R.id.hour)
        mMinthGrid = view.findViewById(R.id.minute)
        mSencondGrid = view.findViewById(R.id.second)
        mCancelView = view.findViewById(R.id.cancel)
        mConfirmView = view.findViewById(R.id.confirm)
        mBlur = view.findViewById(R.id.blur)
        mRootView = view.findViewById(R.id.root)

        val years: MutableList<Int> = ArrayList()
        for (i in 0..23) {
            years.add(i)
        }
        val months: MutableList<Int> = ArrayList()
        for (i in 0..59) {
            months.add(i)
        }
        val sencods: MutableList<Int> = ArrayList()
        for (i in 0..59) {
            sencods.add(i)
        }
        mHourAdapter = DateSelectAdapter(activity!!, inflater, years, newHourCallback())
        mMinuteAdapter = DateSelectAdapter(activity!!, inflater, months, newMinthCallback())
        mSecondAdapter = DateSelectAdapter(activity!!, inflater, sencods, newSecondCallback())
    }

    override fun initBefore(inflater: LayoutInflater, view: View) {
        super.initBefore(inflater, view)
        mCancelView!!.setOnClickListener(this)
        mConfirmView!!.setOnClickListener(this)
    }

    override fun initBind(inflater: LayoutInflater, view: View) {
        super.initBind(inflater, view)
        val hourIndex = calendar[Calendar.HOUR_OF_DAY]
        val minthIndex = calendar[Calendar.MINUTE]
        val secondIndex = calendar[Calendar.SECOND]

        mHourGrid!!.adapter = mHourAdapter
        mMinthGrid!!.adapter = mMinuteAdapter
        mSencondGrid!!.adapter = mSecondAdapter

        mHourGrid!!.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        mMinthGrid!!.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        mSencondGrid!!.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)

        mHourGrid!!.selectedPosition = hourIndex
        mMinthGrid!!.selectedPosition = minthIndex
        mSencondGrid!!.selectedPosition = secondIndex

        mHourAdapter!!.setSelect(mHourAdapter!!.getDataList()[mHourGrid!!.selectedPosition])
        mMinuteAdapter!!.setSelect(mMinuteAdapter!!.getDataList()[mMinthGrid!!.selectedPosition])
        mSecondAdapter!!.setSelect(mSecondAdapter!!.getDataList()[mSencondGrid!!.selectedPosition])
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    override fun isMaterial(): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blur(mRootView, mBlur)
    }

    override fun getWidthAndHeight(): IntArray {
        return intArrayOf(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun getDimAmount(): Float {
        return 0f
    }

    private fun newHourCallback(): DateSelectAdapter.Callback {
        return object : DateSelectAdapter.Callback {
            override fun onClick(bean: Int?) {
                calendar[Calendar.HOUR_OF_DAY] = bean!!
            }
        }
    }

    private fun newMinthCallback(): DateSelectAdapter.Callback {
        return object : DateSelectAdapter.Callback {
            override fun onClick(bean: Int?) {
                calendar[Calendar.MINUTE] = bean!! - 1
            }
        }
    }

    private fun newSecondCallback(): DateSelectAdapter.Callback {
        return object : DateSelectAdapter.Callback {
            override fun onClick(bean: Int?) {
                calendar[Calendar.SECOND] = bean!! - 1
            }
        }
    }

    override fun onClick(v: View) {
        if (v == mCancelView) {
            dismiss()
        } else if (v == mConfirmView) {
            if (callback != null) callback!!.onConfirm(calendar.timeInMillis)
            dismiss()
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onConfirm(timeMills: Long)
    }

    companion object {
        const val TAG: String = "TimePickerDialog"
        fun newInstance(): TimePickerDialog {
            val args = Bundle()

            val fragment = TimePickerDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
