package com.soya.launcher.ui.fragment

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.appContext
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.adapter.DateListAdapter
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.bean.DateItem
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.SimpleTimeZone
import com.soya.launcher.databinding.FragmentSetDateBinding
import com.soya.launcher.ui.dialog.DatePickerDialog
import com.soya.launcher.ui.dialog.TimePickerDialog
import com.soya.launcher.ui.dialog.TimeZoneDialog
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.AppUtils
import java.util.Arrays
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

abstract class AbsDateFragment<VDB : FragmentSetDateBinding, VM : BaseViewModel> : BaseWallPaperFragment<VDB,VM>(), View.OnClickListener {

    private var mItemAdapter: DateListAdapter? = null

    private val itemList: MutableList<DateItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val is24 = AppUtils.is24Display(activity)
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

        setContent()
        setSlide()
    }


    private fun setContent() {
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                activity,
                LayoutInflater.from(appContext),
                newCallback(),
                R.layout.holder_setting
            )
        )
        val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_MEDIUM,
            false
        )
        mBind.content.adapter = itemBridgeAdapter
        mBind.content.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)

        arrayObjectAdapter.addAll(
            0,
            listOf(SettingItem(1, getString(R.string.network), R.drawable.baseline_wifi_100))
        )
    }

    private fun setSlide() {
        mItemAdapter =
            DateListAdapter(activity, LayoutInflater.from(appContext), itemList, object : DateListAdapter.Callback {
                override fun onClick(bean: DateItem) {
                    when (bean.type) {
                        0 -> changAutoTime(bean)
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

                        3 -> chang24Display(bean)
                        4 -> openTimeZone(bean)
                    }
                }
            })
        mBind.slide.adapter = mItemAdapter
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
                mItemAdapter!!.notifyDataSetChanged()
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
                AppUtils.setTime(timeMills)
                itemList[1].description = date
                mItemAdapter!!.notifyItemRangeChanged(0, itemList.size)
            }
        })
        dialog.show(childFragmentManager, DatePickerDialog.TAG)
    }

    private fun changAutoTime(bean: DateItem) {
        try {
            val isAuto = isAutoTime
            AppUtils.setAutoDate(activity, !isAuto)
            bean.description = if (!isAuto) getString(R.string.open) else getString(R.string.close)
            bean.isSwitch = !isAuto
            mItemAdapter!!.notifyItemRangeChanged(itemList.indexOf(bean), itemList.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun chang24Display(bean: DateItem) {
        try {
            val isAuto = AppUtils.is24Display(activity)
            AppUtils.set24Display(activity, !isAuto)
            bean.description = if (!isAuto) getString(R.string.open) else getString(R.string.close)
            bean.isSwitch = !isAuto
            mItemAdapter!!.notifyItemRangeChanged(itemList.indexOf(bean), itemList.size)
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
                AppUtils.setTime(timeMills)
                itemList[2].description = time
                mItemAdapter!!.notifyItemRangeChanged(0, itemList.size)
            }
        })
        dialog.show(childFragmentManager, TimePickerDialog.TAG)
    }

    fun newCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem) {
            }

            override fun onClick(bean: SettingItem) {
                when (bean.type) {
                    0 -> AndroidSystem.openDateSetting(requireContext())
                    1 -> AndroidSystem.openWifiSetting(requireContext())
                }
            }
        }
    }

    override fun onClick(v: View) {
        if (v == mBind.next) {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, GuideWifiListFragment.newInstance())
                .addToBackStack(null).commit()
        }
    }


    protected fun showWifi(show: Boolean) {
        mBind.content.visibility = if (show) View.VISIBLE else View.GONE
    }

    protected fun showNext(show: Boolean) {
        mBind.next.visibility = if (show) View.VISIBLE else View.GONE
    }
}
