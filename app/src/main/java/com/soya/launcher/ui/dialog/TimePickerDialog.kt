package com.soya.launcher.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.clickNoRepeat
import com.soya.launcher.R
import com.soya.launcher.databinding.DialogTimePickerBinding
import com.soya.launcher.databinding.HolderDateSelectBinding
import java.util.Calendar

class TimePickerDialog : SingleDialogFragment<DialogTimePickerBinding>(), View.OnClickListener {
    private val calendar: Calendar = Calendar.getInstance()

   


    private var callback: Callback? = null


    private var selectIndexHour: Int? = null
    private var selectIndexMinute: Int? = null
    private var selectIndexSecond: Int? = null
    override fun init( view: View) {

        val hours: MutableList<Int> = ArrayList()
        for (i in 0..23) {
            hours.add(i)
        }
        val minutes: MutableList<Int> = ArrayList()
        for (i in 0..59) {
            minutes.add(i)
        }
        val sencods: MutableList<Int> = ArrayList()
        for (i in 0..59) {
            sencods.add(i)
        }

        binding.hour.setup {
            addType<Int>(R.layout.holder_date_select)
            onBind {
                val dto = getModel<Int>()
                val binding = getBinding<HolderDateSelectBinding>()
                binding.title.isSelected = dto == selectIndexHour
                binding.title.text = dto.toString()
                itemView.clickNoRepeat {
                    selectIndexHour = dto
                    notifyItemRangeChanged(0, mutable.size)
                    calendar[Calendar.HOUR_OF_DAY] = dto
                }
            }
        }.models = hours

        binding.minute.setup {
            addType<Int>(R.layout.holder_date_select)
            onBind {
                val dto = getModel<Int>()
                val binding = getBinding<HolderDateSelectBinding>()
                binding.title.isSelected = dto == selectIndexMinute
                binding.title.text = dto.toString()
                itemView.clickNoRepeat {
                    selectIndexMinute = dto
                    notifyItemRangeChanged(0, mutable.size)
                    calendar[Calendar.MINUTE] = dto - 1
                }
            }
        }.models = minutes

        binding.second.setup {
            addType<Int>(R.layout.holder_date_select)
            onBind {
                val dto = getModel<Int>()
                val binding = getBinding<HolderDateSelectBinding>()
                binding.title.isSelected = dto == selectIndexSecond
                binding.title.text = dto.toString()
                itemView.clickNoRepeat {
                    selectIndexSecond = dto
                    notifyItemRangeChanged(0, mutable.size)
                    calendar[Calendar.SECOND] = dto - 1
                }
            }
        }.models = sencods

    }

    override fun initBefore(view: View) {
        binding.cancel.setOnClickListener(this)
        binding.confirm.setOnClickListener(this)
    }

    override fun initBind( view: View) {
        val hourIndex = calendar[Calendar.HOUR_OF_DAY]
        val minthIndex = calendar[Calendar.MINUTE]
        val secondIndex = calendar[Calendar.SECOND]


        binding.hour.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.minute.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.second.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.hour.selectedPosition = hourIndex
        binding.minute.selectedPosition = minthIndex
        binding.second.selectedPosition = secondIndex

        selectIndexHour = binding.hour.mutable[binding.hour.selectedPosition] as Int?
        selectIndexMinute = binding.minute.mutable[binding.hour.selectedPosition] as Int?
        selectIndexSecond = binding.second.mutable[binding.second.selectedPosition] as Int?

    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    override fun isMaterial(): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blur(binding.root, binding.blur)
    }

    override fun getWidthAndHeight(): Pair<Int,Int> {
        return ViewGroup.LayoutParams.MATCH_PARENT to ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun getDimAmount(): Float {
        return 0f
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
        const val TAG: String = "TimePickerDialog"
        fun newInstance(): TimePickerDialog {
            val args = Bundle()

            val fragment = TimePickerDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
