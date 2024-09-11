package com.soya.launcher.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.ext.e
import com.soya.launcher.R
import com.soya.launcher.adapter.TimeZoneAdapter
import com.soya.launcher.bean.SimpleTimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.TimeZone

class TimeZoneDialog : SingleDialogFragment() {
    private var mContentGrid: VerticalGridView? = null

    private var mTimeZoneAdapter: TimeZoneAdapter? = null

    private var mBlur: ImageView? = null
    private var mRootView: View? = null

    private var callback: Callback? = null


    override fun getLayout(): Int {
        return R.layout.dialog_time_zone
    }

    override fun init(inflater: LayoutInflater, view: View) {
        super.init(inflater, view)
        mContentGrid = view.findViewById(R.id.content)
        mBlur = view.findViewById(R.id.blur)
        mRootView = view.findViewById(R.id.root)

        mTimeZoneAdapter = TimeZoneAdapter(requireContext(), inflater, ArrayList())

        blur(mRootView, mBlur)
    }

    override fun initBind(inflater: LayoutInflater, view: View) {
        super.initBind(inflater, view)

        lifecycleScope.launch {
           /* withContext(Dispatchers.Main){
                blur(mRootView, mBlur)
            }*/
            var select = 0
            val list: MutableList<SimpleTimeZone> = ArrayList()
            val aDefault = TimeZone.getDefault()
            withContext(Dispatchers.IO){

                val ids = TimeZone.getAvailableIDs()


                for (i in ids.indices) {
                    val id = ids[i]
                    val zone = TimeZone.getTimeZone(id)
                    list.add(SimpleTimeZone(zone, id, zone.displayName))
                }

                // 排序集合
                Collections.sort(list, Comparator.comparingInt { o: SimpleTimeZone -> o.zone.rawOffset })

                for (i in list.indices) {
                    val id = list[i].zone.id
                    if (id == aDefault.id) select = i
                }
            }
            withContext(Dispatchers.Main){
                mTimeZoneAdapter!!.replace(list)
                mContentGrid!!.adapter = mTimeZoneAdapter
                mContentGrid!!.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT)

                mContentGrid!!.apply {
                   postDelayed({
                       "开始执行"
                       scrollToPosition(select)
                       //mContentGrid!!.selectedPosition = select
                       //mTimeZoneAdapter!!.setSelect(aDefault)
                   },0)
                }

                "当前选中的时区是：${aDefault.displayName}::$select"


                mTimeZoneAdapter!!.setCallback(object :TimeZoneAdapter.Callback{
                    override fun onClick(bean: SimpleTimeZone?) {
                        if (callback != null) callback!!.onClick(bean)
                    }

                })
            }
        }




    }

    override fun getDimAmount(): Float {
        return 0f
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onClick(bean: SimpleTimeZone?)
    }

    companion object {
        const val TAG: String = "TimeZoneDialog"
        @JvmStatic
        fun newInstance(): TimeZoneDialog {
            val args = Bundle()

            val fragment = TimeZoneDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
