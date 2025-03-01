package com.chihihx.launcher.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.clickNoRepeat
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.DialogDatePickerBinding
import com.chihihx.launcher.databinding.HolderDateSelectBinding
import java.util.Calendar

class DatePickerDialog : SingleDialogFragment<DialogDatePickerBinding>(), View.OnClickListener {
    private val calendar: Calendar = Calendar.getInstance()




    private var callback: Callback? = null



    private var selectIndexYear: Int? = null
    private var selectIndexMonth: Int? = null
    private var selectIndexDay: Int? = null
    override fun init( view: View) {

        val years: MutableList<Int> = ArrayList()
        for (i in 0..299) {
            years.add(1900 + i)
        }
        val months: MutableList<Int> = ArrayList()
        for (i in 1..12) {
            months.add(i)
        }
        binding.year.setup {
            addType<Int>(R.layout.holder_date_select)
            onBind {
                val dto = getModel<Int>()
                val binding = getBinding<HolderDateSelectBinding>()
                binding.title.isSelected = dto == selectIndexYear
                binding.title.text = dto.toString()
                itemView.clickNoRepeat {
                    selectIndexYear = dto
                    notifyItemRangeChanged(0, mutable.size)
                    calendar[Calendar.YEAR] = dto
                    syncDay()
                }
            }
        }.models = years

        binding.month.setup {
            addType<Int>(R.layout.holder_date_select)
            onBind {
                val dto = getModel<Int>()
                val binding = getBinding<HolderDateSelectBinding>()
                binding.title.isSelected = dto == selectIndexMonth
                binding.title.text = dto.toString()
                itemView.clickNoRepeat {
                    selectIndexMonth = dto
                    notifyItemRangeChanged(0, mutable.size)
                    calendar[Calendar.MONTH] = dto - 1
                    syncDay()
                }
            }
        }.models = months

        binding.day.setup {
            addType<Int>(R.layout.holder_date_select)
            onBind {
                val dto = getModel<Int>()
                val binding = getBinding<HolderDateSelectBinding>()
                binding.title.isSelected = dto == selectIndexDay
                binding.title.text = dto.toString()
                itemView.clickNoRepeat {
                    selectIndexDay = dto
                    notifyItemRangeChanged(0, mutable.size)
                    calendar[Calendar.DAY_OF_MONTH] = dto
                }
            }
        }

    }

    override fun initBefore( view: View) {
        binding.cancel.setOnClickListener(this)
        binding.confirm.setOnClickListener(this)
    }

    override fun initBind(view: View) {
        val yearIndex = calendar[Calendar.YEAR] - 1900
        val monthIndex = calendar[Calendar.MONTH]
        val dayIndex = calendar[Calendar.DAY_OF_MONTH] - 1


        binding.year.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.month.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.day.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.year.selectedPosition = yearIndex
        binding.month.selectedPosition = monthIndex
        syncDay()
        binding.day.selectedPosition = dayIndex

        selectIndexYear = binding.year.mutable[binding.year.selectedPosition] as Int?
        selectIndexMonth = binding.year.mutable[binding.month.selectedPosition] as Int?
        selectIndexDay = binding.year.mutable[binding.day.selectedPosition] as Int?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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



    private fun syncDay() {
        val max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val days: MutableList<Int> = ArrayList()
        for (i in 1..max) {
            days.add(i)
        }
        binding.day.models = days
    }

    override fun onClick(v: View) {
        if (v == binding.cancel) {
            dismiss()
        } else if (v == binding.confirm) {
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

