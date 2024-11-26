package com.soya.launcher.ui.fragment

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.View
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.DateItem
import com.soya.launcher.bean.SimpleTimeZone
import com.soya.launcher.databinding.FragmentSetDateBinding
import com.soya.launcher.databinding.HolderDateListBinding
import com.soya.launcher.ui.dialog.DatePickerDialog
import com.soya.launcher.ui.dialog.TimePickerDialog
import com.soya.launcher.ui.dialog.TimeZoneDialog
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.utils.AppUtil
import java.util.Arrays
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

abstract class AbsDateFragment<VDB : FragmentSetDateBinding, VM : BaseViewModel> : BaseWallPaperFragment<VDB,VM>(), View.OnClickListener {


    private val itemList: MutableList<DateItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val is24 = AppUtil.is24Display(requireContext())
        itemList.addAll(
            Arrays.asList(
                DateItem(
                    0,
                    getString(R.string.auto_time_title),
                    if (isAutoTime) getString(R.string.open) else getString(R.string.close),
                    isAutoTime,
                    true
                ),
                DateItem(1, getString(R.string.set_date_title), date, false, false),
                DateItem(2, getString(R.string.set_time_title), time, false, false),
                DateItem(
                    3,
                    getString(R.string.time_display),
                    if (is24) getString(R.string.open) else getString(R.string.close),
                    is24,
                    true
                ),
                DateItem(4, getString(R.string.time_zone), TimeZone.getDefault().id, false, false)
            )
        )
    }


    override fun initView() {
        mBind.slide.post {
            mBind.slide.requestFocus()
        }
        mBind.next.setOnClickListener(this)

        setSlide()
    }




    private fun setSlide() {
        mBind.slide.setup {
            addType<DateItem>(R.layout.holder_date_list)
            onBind {
                val binding = getBinding<HolderDateListBinding>()
                val dto = getModel<DateItem>()
                binding.title.isEnabled = !(models as MutableList<DateItem>)[0].isSwitch || (modelPosition != 1 && modelPosition != 2)
                itemView.clickNoRepeat {
                    when (dto.type) {
                        0 -> changAutoTime(dto)
                        1 -> if (isAutoTime) {
                            val dialog = ToastDialog.newInstance(
                                getString(R.string.is_auto_time_toast),
                                ToastDialog.MODE_CONFIRM
                            )
                            dialog.show(childFragmentManager, ToastDialog.TAG)
                        } else {
                            openDatePicker()
                        }

                        2 -> if (isAutoTime) {
                            val dialog = ToastDialog.newInstance(
                                getString(R.string.is_auto_time_toast),
                                ToastDialog.MODE_CONFIRM
                            )
                            dialog.show(childFragmentManager, ToastDialog.TAG)
                        } else {
                            openTimePicker()
                        }

                        3 -> chang24Display(dto)
                        4 -> openTimeZone(dto)
                    }
                }
            }
        }.models = itemList

        mBind.slide.selectedPosition = 0
    }

    private fun openTimeZone(item: DateItem) {
        val dialog = TimeZoneDialog.newInstance()
        dialog.setCallback(object : TimeZoneDialog.Callback {
            override fun onClick(bean: SimpleTimeZone?) {
                var alarmManager: AlarmManager? = null
                alarmManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity!!.getSystemService(AlarmManager::class.java)
                } else {
                    activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                }

                alarmManager!!.setTimeZone(bean!!.zone.id)
                item.description = bean.desc
                itemList[1].description = date
                itemList[2].description = time
                mBind.slide.adapter?.notifyDataSetChanged()
                dialog.dismiss()
            }
        })
        dialog.show(childFragmentManager, TimeZoneDialog.TAG)
    }

    private val date: String
        get() {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1
            val day = calendar[Calendar.DAY_OF_MONTH]
            val name = Locale.getDefault().language
            return if (name == "zh") getString(
                R.string.year_month_day,
                year,
                month,
                day
            ) else getString(R.string.year_month_day, day, month, year)
        }

    private val time: String
        get() {
            val calendar = Calendar.getInstance()
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            val second = calendar[Calendar.SECOND]
            return getString(R.string.hour_minute_second, hour, minute)
        }

    private fun openDatePicker() {
        val dialog = DatePickerDialog.newInstance()
        dialog.setCallback(object : DatePickerDialog.Callback {
            override fun onConfirm(timeMills: Long) {
                AppUtil.setTime(timeMills)
                itemList[1].description = date
                mBind.slide.adapter?.notifyItemRangeChanged(0, itemList.size)
            }
        })
        dialog.show(childFragmentManager, DatePickerDialog.TAG)
    }

    private fun changAutoTime(bean: DateItem) {
        try {
            val isAuto = isAutoTime
            AppUtil.setAutoDate(requireContext(), !isAuto)
            bean.description = if (!isAuto) getString(R.string.open) else getString(R.string.close)
            bean.isSwitch = !isAuto
            mBind.slide.adapter?.notifyItemRangeChanged(itemList.indexOf(bean), itemList.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun chang24Display(bean: DateItem) {
        try {
            val isAuto = AppUtil.is24Display(requireContext())
            AppUtil.set24Display(requireContext(), !isAuto)
            bean.description = if (!isAuto) getString(R.string.open) else getString(R.string.close)
            bean.isSwitch = !isAuto
            mBind.slide.adapter?.notifyItemRangeChanged(itemList.indexOf(bean), itemList.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val isAutoTime: Boolean
        get() = try {
            Settings.Global.getInt(activity!!.contentResolver, Settings.Global.AUTO_TIME) == 1
        } catch (e: SettingNotFoundException) {
            false
        }


    private fun openTimePicker() {
        val dialog = TimePickerDialog.newInstance()
        dialog.setCallback(object : TimePickerDialog.Callback {
            override fun onConfirm(timeMills: Long) {
                AppUtil.setTime(timeMills)
                itemList[2].description = time
                mBind.slide.adapter?.notifyItemRangeChanged(0, itemList.size)
            }
        })
        dialog.show(childFragmentManager, TimePickerDialog.TAG)
    }


    override fun onClick(v: View) {
        if (v == mBind.next) {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, WifiGuidFragment.newInstance())
                .addToBackStack(null).commit()
        }
    }




    protected fun showNext(show: Boolean) {
        mBind.next.visibility = if (show) View.VISIBLE else View.GONE
    }
}
