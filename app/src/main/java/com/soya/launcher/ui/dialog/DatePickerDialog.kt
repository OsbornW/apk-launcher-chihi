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

class DatePickerDialog : SingleDialogFragment(), View.OnClickListener {
    private val calendar: Calendar = Calendar.getInstance()

    private var mYearGrid: HorizontalGridView? = null
    private var mMonthGrid: HorizontalGridView? = null
    private var mDayGrid: HorizontalGridView? = null
    private var mCancelView: View? = null
    private var mConfirmView: View? = null
    private var mBlur: ImageView? = null
    private var mRootView: View? = null

    private var mYearAdapter: DateSelectAdapter? = null
    private var mMonthAdapter: DateSelectAdapter? = null
    private var mDayAdapter: DateSelectAdapter? = null

    private var callback: Callback? = null

    override fun getLayout(): Int {
        return R.layout.dialog_date_picker
    }

    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mYearGrid = view.findViewById(R.id.year)
        mMonthGrid = view.findViewById(R.id.month)
        mDayGrid = view.findViewById(R.id.day)
        mCancelView = view.findViewById(R.id.cancel)
        mConfirmView = view.findViewById(R.id.confirm)
        mBlur = view.findViewById(R.id.blur)
        mRootView = view.findViewById(R.id.root)

        val years: MutableList<Int> = ArrayList()
        for (i in 0..299) {
            years.add(1900 + i)
        }
        val months: MutableList<Int> = ArrayList()
        for (i in 1..12) {
            months.add(i)
        }
        mYearAdapter = DateSelectAdapter(activity!!, inflater, years, newYearCallback())
        mMonthAdapter = DateSelectAdapter(activity!!, inflater, months, newMonthCallback())
        mDayAdapter = DateSelectAdapter(activity!!, inflater, ArrayList(), newDayCallback())
    }

    override fun initBefore(inflater: LayoutInflater, view: View) {
        super.initBefore(inflater, view)
        mCancelView!!.setOnClickListener(this)
        mConfirmView!!.setOnClickListener(this)
    }

    override fun initBind(inflater: LayoutInflater, view: View) {
        super.initBind(inflater, view)
        val yearIndex = calendar[Calendar.YEAR] - 1900
        val monthIndex = calendar[Calendar.MONTH]
        val dayIndex = calendar[Calendar.DAY_OF_MONTH] - 1

        mYearGrid!!.adapter = mYearAdapter
        mMonthGrid!!.adapter = mMonthAdapter
        mDayGrid!!.adapter = mDayAdapter

        mYearGrid!!.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        mMonthGrid!!.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        mDayGrid!!.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)

        mYearGrid!!.selectedPosition = yearIndex
        mMonthGrid!!.selectedPosition = monthIndex
        syncDay()
        mDayGrid!!.selectedPosition = dayIndex

        mYearAdapter!!.setSelect(mYearAdapter!!.getDataList()[mYearGrid!!.selectedPosition])
        mMonthAdapter!!.setSelect(mMonthAdapter!!.getDataList()[mMonthGrid!!.selectedPosition])
        mDayAdapter!!.setSelect(mDayAdapter!!.getDataList()[mDayGrid!!.selectedPosition])
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blur(mRootView, mBlur)
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    override fun isMaterial(): Boolean {
        return false
    }

    override fun getWidthAndHeight(): IntArray {
        return intArrayOf(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun getDimAmount(): Float {
        return 0f
    }

    private fun newYearCallback(): DateSelectAdapter.Callback {
        return object : DateSelectAdapter.Callback {
            override fun onClick(bean: Int?) {
                calendar[Calendar.YEAR] = bean!!
                syncDay()
            }
        }
    }

    private fun newMonthCallback(): DateSelectAdapter.Callback {
        return object : DateSelectAdapter.Callback {
            override fun onClick(bean: Int?) {
                calendar[Calendar.MONTH] = bean!! - 1
                syncDay()
            }
        }
    }

    private fun newDayCallback(): DateSelectAdapter.Callback {
        return object : DateSelectAdapter.Callback {
            override fun onClick(bean: Int?) {
                calendar[Calendar.DAY_OF_MONTH] = bean!!
            }
        }
    }

    private fun syncDay() {
        val max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val days: MutableList<Int> = ArrayList()
        for (i in 1..max) {
            days.add(i)
        }
        mDayAdapter!!.replace(days)
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
        const val TAG: String = "DatePickerDialog"
        fun newInstance(): DatePickerDialog {
            val args = Bundle()

            val fragment = DatePickerDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
