package com.soya.launcher.ui.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.TextUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import b.d
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.d
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.stringValue
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.adapter.WifiListAdapter
import com.soya.launcher.bean.WifiItem
import com.soya.launcher.ui.dialog.ProgressDialog
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.ui.dialog.WifiPassDialog
import com.soya.launcher.ui.dialog.WifiSaveDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.view.MyFrameLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


abstract class AbsWifiListFragment : AbsFragment() {
    private val exec = Executors.newCachedThreadPool()
    private var mWifiEnableView: View? = null

    // 未保存和连接的WiFi列表
    private var mContentGrid: VerticalGridView? = null

    // 已保存的WiFi列表
    private var rvSavedWifi: VerticalGridView? = null

    // 当前已连接的WiFi View
    private var fl_connected: MyFrameLayout? = null

    // 左侧面板 WiFi名字
    private var mWifiNameView: TextView? = null
    private var mNetwordConnectedView: TextView? = null
    private var mIPView: TextView? = null
    private var mSignalView: TextView? = null
    private var mLockView: ImageView? = null


    private var mWifiSwitch: Switch? = null
    private var mOffView: View? = null
    private var mProgressBar: View? = null
    private var mNextView: View? = null
    private var mLoopProgress: View? = null
    private var mWifiManager: WifiManager? = null
    private var mAdapter: WifiListAdapter? = null
    private var receiver: WifiReceiver? = null
    private var mDialog: ProgressDialog? = null
    private var uiHandler: Handler? = null
    private var mTarget: ScanResult? = null
    private var connectedTime: Long = -1

    //当前连接的WiFi名称
    private var tvConnectedName: TextView? = null

    // 当前WiFi连接状态
    private var tvConnectStatus: TextView? = null

    //当前连接WiFi信号值
    private var ivConectedSignal: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWifiManager = activity!!.getSystemService(WifiManager::class.java)
        mDialog = ProgressDialog.newInstance()
        uiHandler = Handler()
        receiver = WifiReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        activity!!.registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        exec.shutdownNow()
        activity!!.unregisterReceiver(receiver)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_wifi_list
    }

    fun setRVSaveHeight(height: Int) {
        rvSavedWifi?.post {
            rvSavedWifi?.layoutParams?.height = height
            rvSavedWifi?.requestLayout() // 强制重新布局
        }
    }

    @SuppressLint("MissingPermission")
    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        tvConnectedName = view.findViewById(R.id.tv_connected_wifiname)
        tvConnectStatus = view.findViewById(R.id.tv_connected_status)
        ivConectedSignal = view.findViewById(R.id.tv_connected_signal)
        mLockView = view.findViewById(R.id.iv_ock)
        mWifiEnableView = view.findViewById(R.id.wifi_enable)
        mContentGrid = view.findViewById(R.id.content)
        rvSavedWifi = view.findViewById(R.id.rv_saved_wifi)
        fl_connected = view.findViewById(R.id.wifi_connected)
        mWifiNameView = view.findViewById(R.id.wifi_name)
        mNetwordConnectedView = view.findViewById(R.id.netword_connected_mes)
        mIPView = view.findViewById(R.id.ip)
        mSignalView = view.findViewById(R.id.signal)
        mWifiSwitch = view.findViewById(R.id.switch_item)
        mOffView = view.findViewById(R.id.off)
        mProgressBar = view.findViewById(R.id.progressBar)
        mNextView = view.findViewById(R.id.next)
        mLoopProgress = view.findViewById(R.id.loop)
        mAdapter =
            WifiListAdapter(activity!!, inflater, CopyOnWriteArrayList(), useNext(), newCallback())
        mAdapter!!.replace(fillterWifi(mWifiManager!!.scanResults))


        fl_connected?.let {
            it.clickNoRepeat {
                val info = mWifiManager!!.connectionInfo
                val dialog = WifiSaveDialog.newInstance(info.ssid,R.string.disconnect.stringValue())
                dialog.setCallback(object :WifiSaveDialog.Callback{
                    override fun onClick(type: Int) {
                        when (type) {
                            0 -> {
                               mWifiManager?.disableNetwork(info!!.networkId)
                            }

                            -1 -> {
                                mWifiManager!!.removeNetwork(info!!.networkId)
                            }
                        }
                    }

                })
                dialog.show(getChildFragmentManager(), WifiSaveDialog.TAG)
            }
        }

        /*
        * wifi是否关闭状态
        * */
        mWifiManager?.let {
            (it.isWifiEnabled).yes {
                mOffView?.isVisible = false
                mContentGrid?.isVisible = true
                rvSavedWifi?.isVisible = true
                fl_connected?.isVisible = true
            }.otherwise {
                mOffView?.isVisible = true
                mContentGrid?.isVisible = false
                rvSavedWifi?.isVisible = false
                fl_connected?.isVisible = false
            }
        }


        // 已保存WiFi列表item 点击回调
        var myCallback: WifiListAdapter.Callback = savedCallback()


        /**
         * 已保存WiFi列表 控件初始化和数据绑定
         */
        rvSavedWifi?.linear()
            ?.setup {


                addType<WifiItem>(R.layout.holder_wifi)
                onBind {

                    /*控件初始化开始*/
                    val tvWifiName = findView<TextView>(R.id.title)
                    val tvConnectStatus = findView<TextView>(R.id.status)
                    val ivSignal = findView<ImageView>(R.id.signal)
                    val ivLock = findView<ImageView>(R.id.lock)
                    //val flRoot = findView<FrameLayout>(R.id.fl_root)
                    val flRoot = findView<MyFrameLayout>(R.id.fl_root)
                    /*控件初始化结束*/


                    // 获取当前Item
                    val bean = _data as WifiItem

                    val result = bean.item
                    //是否无需密码的免费WiFi
                    val usePass = AndroidSystem.isUsePassWifi(result)
                    //设置连接状态
                    tvConnectStatus.text =
                        if (bean.isSave) context.getString(R.string.saved) else ""
                    //设置WiFi名字
                    tvWifiName.text = result.SSID
                    // WiFi是否需要密码
                    ivLock.visibility = if (usePass) View.VISIBLE else View.GONE
                    //设置WiFi信号状态
                    when (WifiManager.calculateSignalLevel(bean.item.level, 5)) {
                        1, 2 -> ivSignal.setImageResource(R.drawable.baseline_wifi_1_bar_100)
                        3 -> ivSignal.setImageResource(R.drawable.baseline_wifi_2_bar_100)
                        else -> ivSignal.setImageResource(R.drawable.baseline_wifi_100)
                    }

                    //设置Item回调
                    itemView.setOnClickListener {
                        myCallback.onClick(bean)
                    }

                }
            }?.models = arrayListOf()


        //列表加载进度条是否可见
        (mWifiManager?.isWifiEnabled ?: true).yes {
            mProgressBar?.setVisibility(
                if (mAdapter!!.itemCount == 0) View.VISIBLE
                else View.GONE
            )
        }

        if (useNext()) {
            mWifiEnableView?.nextFocusLeftId = R.id.next
            mWifiEnableView?.nextFocusRightId = R.id.next
        }
    }

    override fun initBefore(view: View, inflater: LayoutInflater) {
        super.initBefore(view, inflater)
        mWifiEnableView!!.setOnClickListener {
            val isEnable = !mWifiManager!!.isWifiEnabled
            mWifiManager!!.setWifiEnabled(isEnable)


            mWifiSwitch!!.isChecked = isEnable

            (isEnable && mAdapter!!.itemCount == 0).yes {
                mProgressBar?.isVisible = true
            }.otherwise {
                mProgressBar?.isVisible = false
            }

            (isEnable).yes {
                mOffView?.isVisible = false
                mContentGrid?.isVisible = true
                rvSavedWifi?.isVisible = true
                fl_connected?.isVisible = true
            }.otherwise {
                mOffView?.isVisible = true
                mContentGrid?.isVisible = false
                rvSavedWifi?.isVisible = false
                fl_connected?.isVisible = false
            }

        }
        mNextView!!.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, ChooseWallpaperFragment.newInstance())
                .addToBackStack(null).commit()
        }
        mDialog!!.setCallback { connectedTime = -1 }
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        mContentGrid!!.setAdapter(mAdapter)
        mWifiSwitch!!.isChecked = mWifiManager!!.isWifiEnabled
        exec.execute(Runnable {
            var time = 0
            while (isAdded) {
                val info = mWifiManager!!.connectionInfo
                uiHandler!!.post(Runnable {
                    if (!isAdded) return@Runnable
                    if (mTarget != null && connectedTime != -1L && TimeUnit.MILLISECONDS.toSeconds(
                            System.currentTimeMillis() - connectedTime
                        ) >= 30
                    ) {
                        toastFail()
                        closeDialog()
                        mTarget = null
                        connectedTime = -1
                    }
                    if (!TextUtils.isEmpty(info.ssid) && info.ipAddress != 0 && info.ssid != "<unknown ssid>") {

                        if (mTarget != null && connectedTime != -1L && mTarget!!.SSID == cleanSSID(
                                info.ssid
                            )
                        ) {
                            closeDialog()
                            mTarget = null
                            connectedTime = -1
                        }
                        if (mTarget == null && connectedTime == -1L) {
                            closeDialog()
                            mTarget = null
                            connectedTime = -1
                        }
                    }
                    val old = mAdapter!!.connectSSID
                    val ssid = cleanSSID(info.ssid)
                    (ssid.contains("ssid")).no {
                        mAdapter!!.connectSSID = ssid
                    }

                    for (item in mAdapter!!.getDataList()) {
                        if (item.item.SSID == ssid) {
                            val index = mAdapter!!.getDataList().indexOf(item)
                            if (index != -1) {
                                mAdapter!!.notifyItemChanged(index)
                            }
                        }
                        if (item.item.SSID == old) {
                            val index = mAdapter!!.getDataList().indexOf(item)
                            if (index != -1) {
                                mAdapter!!.notifyItemChanged(index)
                            }
                        }
                    }
                    syncWifi(info)
                })
                if (time++ % 5 == 0) mWifiManager!!.startScan()
                SystemClock.sleep(1000)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mWifiEnableView)
    }

    override fun getWallpaperView(): Int {
        return R.id.wallpaper
    }

    private fun toastFail() {
        if (!isAdded) return
        val dialog = ToastDialog.newInstance(
            getString(R.string.unable_to_wifi, mTarget!!.SSID),
            ToastDialog.MODE_CONFIRM
        )
        dialog.show(getChildFragmentManager(), ToastDialog.TAG)
    }

    fun wifiRequirePassword(wifiInfo: WifiInfo): Boolean {
        return wifiInfo.networkId == -1
    }

    private fun syncWifi(info: WifiInfo) {
        val level = WifiManager.calculateSignalLevel(info.rssi, 5)
        var levelStr = getString(R.string.signal_low)
        levelStr = when (level) {
            1, 2 -> getString(R.string.signal_low)
            3 -> getString(R.string.signal_middle)
            else -> getString(R.string.signal_strong)
        }
        val isConnected = !TextUtils.isEmpty(info.ssid) && info.ipAddress != 0

        mIPView!!.text = Formatter.formatIpAddress(info.ipAddress)
        mSignalView!!.text = if (isConnected) levelStr else "-"
        mNetwordConnectedView?.text =
            if (isConnected) getString(R.string.connected) else R.string.connecting.stringValue()
        tvConnectStatus?.text =
            if (isConnected) getString(R.string.connected) else R.string.connecting.stringValue()

        (info.ssid.contains("unknown")).yes {
            mWifiNameView?.text = ""
            fl_connected?.isVisible = false
        }.otherwise {
            fl_connected?.isVisible = true
            mWifiNameView!!.text = getNoDoubleQuotationSSID(info.ssid)
            tvConnectedName!!.text = getNoDoubleQuotationSSID(info.ssid)
        }

        var isNeedPwd = true

        items?.let {
            it.forEach {wifiItem->
                if(wifiItem.item.SSID==getNoDoubleQuotationSSID(info.ssid)){
                    isNeedPwd = AndroidSystem.isUsePassWifi(wifiItem.item)
                }
            }
        }

        isNeedPwd.yes {
            mLockView?.isVisible = true
        }.otherwise {
            mLockView?.isVisible = false
        }


        /**
         * 已保存列表中的WiFi连接成功后，从已保存列表中进行移除
         * */
        isConnected.yes {
            ((rvSavedWifi?.mutable as MutableList<WifiItem>).size > 0).yes {
                for (i in (rvSavedWifi?.mutable as MutableList<WifiItem>).size - 1 downTo 0) {

                    if ((rvSavedWifi?.mutable as MutableList<WifiItem>)[i].item.SSID == mAdapter!!.connectSSID) {
                        (rvSavedWifi?.mutable as MutableList<WifiItem>).removeAt(i)
                        rvSavedWifi?.adapter?.notifyDataSetChanged()

                        fl_connected?.post {
                            fl_connected?.requestFocus()

                        }
                    }
                }

                ((rvSavedWifi?.mutable as MutableList<WifiItem>).size > 2).yes {
                    setRVSaveHeight(com.shudong.lib_dimen.R.dimen.qb_px_120.dimenValue())
                }.otherwise {
                    setRVSaveHeight(RecyclerView.LayoutParams.WRAP_CONTENT)
                }

            }
        }

        // 信号状态更新
        if (level == 1 || level == 2) {
            ivConectedSignal!!.setImageResource(R.drawable.baseline_wifi_1_bar_100)
        } else if (level == 3) {
            ivConectedSignal!!.setImageResource(R.drawable.baseline_wifi_2_bar_100)
        } else {
            ivConectedSignal!!.setImageResource(R.drawable.baseline_wifi_100)
        }
    }

    /**
     * 系统返回的WiFi名字会出现携带双引号，需进行转义替换
     * 获取无双引号的WiFi name
     */
    fun getNoDoubleQuotationSSID(ssid: String): String {

        //获取Android版本号
        var ssid = ssid
        val deviceVersion = Build.VERSION.SDK_INT
        if (deviceVersion >= 17) {
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length - 1)
            }
        }
        return ssid
    }

    private fun cleanSSID(ssid: String): String {
        return ssid.replaceFirst("^\"".toRegex(), "").replaceFirst("\"$".toRegex(), "")
    }


    /**
     * WiFi列表点击事件处理
     */
    private fun newCallback(): WifiListAdapter.Callback {
        return object : WifiListAdapter.Callback {
            @SuppressLint("MissingPermission")
            override fun onClick(wifiItem: WifiItem?) {
                val bean = wifiItem!!.item
                val usePass = AndroidSystem.isUsePassWifi(bean)
                val saves = mWifiManager!!.configuredNetworks
                val map: MutableMap<String, WifiConfiguration> = HashMap()
                for (item in saves) {
                    val ssd = cleanSSID(item.SSID)
                    map[ssd] = item
                }
                val item = map[bean.SSID]
                val isSave = item != null
                if (isSave) {
                    val dialog = WifiSaveDialog.newInstance(bean.SSID,"")
                    dialog.setCallback (object :WifiSaveDialog.Callback{
                        override fun onClick(type: Int) {
                            val index = mAdapter!!.getDataList().indexOf(wifiItem)
                            when (type) {
                                0 -> {
                                    connect(bean, item!!.networkId)
                                    wifiItem.isSave = true
                                }

                                -1 -> {
                                    mWifiManager!!.removeNetwork(item!!.networkId)
                                    wifiItem.isSave = false
                                }
                            }
                            if (index != -1) mAdapter!!.notifyItemChanged(index)
                        }

                    })
                    dialog.show(getChildFragmentManager(), WifiSaveDialog.TAG)
                } else if (usePass) {
                    val dialog = WifiPassDialog.newInstance(getNoDoubleQuotationSSID(wifiItem.item.SSID))
                    dialog.setDefaultPwd(getNoDoubleQuotationSSID(wifiItem.item.SSID))
                    dialog.setCallback(object :WifiPassDialog.Callback{
                        override fun onConfirm(text: String) {
                            wifiItem.isSave = true
                            val index = mAdapter!!.getDataList().indexOf(wifiItem)
                            if (index != -1){
                                mAdapter?.getDataList()?.removeAt(index)
                                mAdapter?.notifyItemRemoved(index)
                            }
                            //if (index != -1) mAdapter!!.notifyItemChanged(index)
                            connect(bean, text)
                        }

                    })
                    dialog.show(getChildFragmentManager(), WifiPassDialog.TAG)
                } else {
                    wifiItem.isSave = true
                    val index = mAdapter!!.getDataList().indexOf(wifiItem)
                    if (index != -1) mAdapter!!.notifyItemChanged(index)
                    connect(bean, "")
                }
            }
        }
    }

    private fun savedCallback(): WifiListAdapter.Callback {
        return object : WifiListAdapter.Callback {
            @SuppressLint("MissingPermission")
            override fun onClick(wifiItem: WifiItem?) {
                val bean = wifiItem!!.item
                val usePass = AndroidSystem.isUsePassWifi(bean)
                val saves = mWifiManager!!.configuredNetworks
                val map: MutableMap<String, WifiConfiguration> = HashMap()
                for (item in saves) {
                    val ssd = cleanSSID(item.SSID)
                    map[ssd] = item
                }
                val item = map[bean.SSID]
                val isSave = item != null
                if (isSave) {
                    val dialog = WifiSaveDialog.newInstance(bean.SSID,"")
                    dialog.setCallback (object :WifiSaveDialog.Callback{
                        override fun onClick(type: Int) {
                            val index = rvSavedWifi?.mutable?.indexOf(wifiItem) ?: 0
                            when (type) {
                                0 -> {
                                    connect(bean, item!!.networkId)
                                    wifiItem.isSave = true
                                }

                                -1 -> {
                                    mWifiManager!!.removeNetwork(item!!.networkId)
                                    wifiItem.isSave = false
                                    if (index != -1){
                                        rvSavedWifi?.mutable?.removeAt(index)
                                        rvSavedWifi?.adapter?.notifyItemRemoved(index)
                                    }

                                    //rvSavedWifi?.adapter?.notifyDataSetChanged()

                                    //rvSavedWifi?.mutable?.clear()
                                    //rvSavedWifi?.adapter?.notifyDataSetChanged()

                                    setRVSaveHeight(RecyclerView.LayoutParams.WRAP_CONTENT)

                                }
                            }
                        }

                    })
                    dialog.show(getChildFragmentManager(), WifiSaveDialog.TAG)
                } else if (usePass) {
                    val dialog = WifiPassDialog.newInstance(getNoDoubleQuotationSSID(wifiItem.item.SSID))
                    dialog.setCallback (object :WifiPassDialog.Callback{
                        override fun onConfirm(text: String) {
                            wifiItem.isSave = true
                            val index = rvSavedWifi?.mutable?.indexOf(wifiItem) ?: 0
                            if (index != -1) rvSavedWifi?.adapter!!.notifyItemChanged(index)
                            connect(bean, text)
                        }

                    })
                    dialog.show(getChildFragmentManager(), WifiPassDialog.TAG)
                } else {
                    wifiItem.isSave = true
                    val index = rvSavedWifi?.mutable?.indexOf(wifiItem) ?: 0
                    if (index != -1) rvSavedWifi?.adapter!!.notifyItemChanged(index)
                    connect(bean, "")
                }
            }
        }
    }

    private fun showDialog() {
        mLoopProgress!!.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(activity, R.anim.loop_translate)
        mLoopProgress!!.startAnimation(animation)
        //mDialog.show(getChildFragmentManager(), ProgressDialog.TAG);
    }

    private fun closeDialog() {
        mLoopProgress!!.visibility = View.GONE
        if (mLoopProgress!!.animation != null) mLoopProgress!!.animation.cancel()
        //if (mDialog.isAdded() && mDialog.isVisible()) mDialog.dismiss();
    }

    private fun connect(bean: ScanResult, networkId: Int) {
        target(bean)
        mWifiManager!!.enableNetwork(networkId, true)
        showDialog()
    }

    private fun connect(bean: ScanResult, password: String) {
        target(bean)
        val configuration = newWifiConfiguration(bean, password)
        val id = mWifiManager!!.addNetwork(configuration)
        mWifiManager!!.enableNetwork(id, true)
        showDialog()
    }

    private fun target(bean: ScanResult) {
        mTarget = bean
        connectedTime = System.currentTimeMillis()
    }

    protected open fun useNext(): Boolean {
        return true
    }

    var items:MutableList<WifiItem>?=null
    @SuppressLint("MissingPermission")
    private fun availableAction(intent: Intent) {

        val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
        val results = mWifiManager!!.scanResults
         items = fillterWifi(results)

        items?.let {


            it.forEach {
                //"所有的WiFi数据======${it.item.SSID}===是否保存==${it.isSave}".d("zy1996")
            }
            val adds: MutableList<WifiItem> = ArrayList()
            val removes: MutableList<WifiItem> = ArrayList()
            val resultSet: MutableMap<String, WifiItem> = HashMap()
            val sourceSet: MutableMap<String, WifiItem> = HashMap()
            for (result in it) resultSet[result.item.SSID] = result
            for (item in mAdapter!!.getDataList()) sourceSet[item.item.SSID] = item
            for ((key, value) in resultSet) {
                if (!sourceSet.containsKey(key)) adds.add(value)
            }
            for ((key, value) in sourceSet) {
                if (!resultSet.containsKey(key)) {
                    removes.add(value)
                }
            }
            for ((key, value) in resultSet) {
                if (sourceSet.containsKey(key)) {
                    sourceSet[key]!!.item = value.item
                }
            }
            val strongSignalList: MutableList<WifiItem> = arrayListOf()
            val weakSignalList: MutableList<WifiItem> = arrayListOf()
            for (item in adds) {
                if (WifiManager.calculateSignalLevel(item.item.level, 5) >= 4) {
                    strongSignalList.add(item)
                } else {
                    weakSignalList.add(item)
                }
            }
            c.clear()
            c.addAll(strongSignalList)
            c.addAll(weakSignalList)

            c.forEachIndexed { index, item ->

                if (item.isSave && item.item.SSID != mAdapter!!.connectSSID) {
                    d.add(item)
                }
            }

            val e = d.distinctBy { it.item.SSID }
            val cur = rvSavedWifi?.mutable as MutableList<WifiItem>

            //rvSavedWifi?.mutable?.clear()
            val repeat = it.distinctBy { it.item.SSID }

            val filteredList = repeat.filterNot { it in cur }

            filteredList.forEach {
                it.isSave.yes {
                    //"当前过滤后的 集合是=====${it.item.SSID}".d("zy1996")
                }

            }


            filteredList.forEach {

                it.isSave.yes {
                    // "当前即将要添加的已保存的WIFI数据是===${wifiItem.item.SSID}".d("zy1996")
                    val saveList = (rvSavedWifi?.mutable as MutableList<WifiItem>)
                    var isExist = false
                    saveList.forEach { wifiItem ->
                        if (it.item.SSID == wifiItem.item.SSID) {
                            isExist = true
                        }
                    }
                    if (!isExist) {
                        rvSavedWifi?.mutable?.add(it)
                        rvSavedWifi?.adapter?.notifyItemInserted(
                            rvSavedWifi?.mutable?.size ?: 0 - 1
                        )
                    }
                    //"当前集合里面是否包含这个WIFI？${it?.item?.SSID}===${isContain}".d("zy1996")

                }


            }

            //val aList = rvSavedWifi?.mutable as MutableList<WifiItem>

            /* aList.forEach {
            "当前集合中有哪些WIFI===${it.item.SSID}===".d("zy1996")
        }*/

            /*
        (aList)?.let {
            (repeat).forEach {wifiItem->
                "当前是否包含这个WIFI===${wifiItem.item.SSID}===${it.contains(wifiItem)}".d("zy1996")

                (it.contains(wifiItem)).no{
                    wifiItem.isSave.yes {
                        "当前即将要添加的已保存的WIFI数据是===${wifiItem.item.SSID}".d("zy1996")
                        rvSavedWifi?.mutable?.add(wifiItem)
                        rvSavedWifi?.adapter?.notifyItemInserted(rvSavedWifi?.mutable?.size ?: 0 - 1)
                    }

                }
            }
        }*/


            /* filteredList.forEach {
            it.isSave.yes {
                "当前要添加的已保存WiFi是=====${it.item.SSID}".d("zy1996")
                rvSavedWifi?.mutable?.add(it)
                rvSavedWifi?.adapter?.notifyItemInserted(rvSavedWifi?.mutable?.size ?: 0 - 1)
            }
        }*/

            for (i in (rvSavedWifi?.mutable as MutableList<WifiItem>).size - 1 downTo 0) {

                if ((rvSavedWifi?.mutable as MutableList<WifiItem>)[i].item.SSID == mAdapter!!.connectSSID
                ) {
                    (rvSavedWifi?.mutable as MutableList<WifiItem>).removeAt(i)
                    rvSavedWifi?.adapter?.notifyItemRemoved(i)

                }
            }


            ((rvSavedWifi?.mutable as MutableList<WifiItem>).size > 2).yes {
                setRVSaveHeight(com.shudong.lib_dimen.R.dimen.qb_px_120.dimenValue())
            }.otherwise {
                setRVSaveHeight(RecyclerView.LayoutParams.WRAP_CONTENT)
            }


            //val filteredList = e.filterNot { it in cur }


            /* if (filteredList.isNotEmpty()) {

            rvSavedWifi?.mutable?.forEach {
                val bean = it as WifiItem


            }


            for (i in (rvSavedWifi?.mutable as MutableList<WifiItem>).size - 1 downTo 0) {

                if ((rvSavedWifi?.mutable as MutableList<WifiItem>)[i].item.SSID == mAdapter!!.connectSSID
                ) {
                    (rvSavedWifi?.mutable as MutableList<WifiItem>).removeAt(i)
                    rvSavedWifi?.adapter?.notifyItemRemoved(i)

                }
            }

            (rvSavedWifi?.mutable as MutableList<WifiItem>).forEachIndexed { index, wifiItem ->
                "当前已保存列表里面的是=====${wifiItem.item.SSID}===索引==$index".d("zy1996")
            }

            "当前已保存列表里面的大小是是=====${rvSavedWifi?.mutable?.size}===索引==".d("zy1996")


            ((rvSavedWifi?.mutable as MutableList<WifiItem>).size > 2).yes {
                setRVSaveHeight(com.shudong.lib_dimen.R.dimen.qb_px_120.dimenValue())
            }.otherwise {
                setRVSaveHeight(RecyclerView.LayoutParams.WRAP_CONTENT)
            }

        }*/

            mAdapter?.let {
                for (i in (it.getDataList()).size - 1 downTo 0) {
                    if ((it.getDataList())[i].item.SSID == mAdapter?.connectSSID) {
                        (it.getDataList()).removeAt(i)
                        mAdapter?.notifyItemRemoved(i)
                    }
                    rvSavedWifi?.let { it1 ->
                        for (j in (it1.mutable).size - 1 downTo 0) {
                            if ((it.getDataList())[i].item.SSID == ((it1.mutable)[j] as WifiItem).item.SSID) {
                                (it.getDataList()).removeAt(i)
                                mAdapter?.notifyItemRemoved(i)
                            }
                        }

                    }

                }
            }

            for (i in (c).size - 1 downTo 0) {
                if (c[i].isSave || c[i].item.SSID == mAdapter!!.connectSSID) {
                    (c).removeAt(i)
                }
            }




            mAdapter?.let {
                for (i in (it.getDataList()).size - 1 downTo 0) {


                    for (j in (c).size - 1 downTo 0) {
                        if ((it.getDataList())[i].item.SSID == (c)[j].item.SSID) {
                            (it.getDataList()).removeAt(i)
                            mAdapter?.notifyItemRemoved(i)
                        }
                    }


                }
            }

            /*  mAdapter?.let {
              for (i in (it.getDataList()).size - 1 downTo 0) {
                  if(it.getDataList()[i].isSave||it.getDataList()[i].item.SSID==mAdapter!!.connectSSID){
                      (it.getDataList()).removeAt(i)
                      mAdapter?.notifyItemRemoved(i)
                  }
              }
          }*/


            val newList = c.distinctBy { it.item.SSID }.toMutableList()

            mAdapter!!.remove(removes)
            mAdapter!!.add(mAdapter!!.itemCount, newList)

            mAdapter?.let {
                it.getDataList().forEach {
                    //"当前要未添加和保存WiFi是=====${it.item.SSID}".d("zy1996")
                }
            }

            mProgressBar!!.visibility =
                if (mAdapter!!.itemCount == 0 && mOffView!!.visibility == View.GONE) View.VISIBLE else View.GONE

        }
    }

    private var c: MutableList<WifiItem> = arrayListOf()
    private var d: MutableList<WifiItem> = arrayListOf()

    @SuppressLint("MissingPermission")
    private fun fillterWifi(results: MutableList<ScanResult>): MutableList<WifiItem> {
        val list: MutableList<WifiItem> = mutableListOf()
        val saves = mWifiManager!!.configuredNetworks
        saves.forEach {
            //"当前的已保存的WIFI数据是===${it.SSID}".d("zy1996")
        }
        val map: MutableMap<String, WifiConfiguration> = HashMap()
        for (item in saves) map[cleanSSID(item.SSID)] = item
        for (result in results) {
            if (!TextUtils.isEmpty(result.SSID)) {
                val item = map[result.SSID]
                val isSave = item != null
                list.add(WifiItem(result, isSave))
            }
        }
        list.sortWith { o1, o2 ->
            if (WifiManager.calculateSignalLevel(
                    o1.item.level,
                    5
                ) > WifiManager.calculateSignalLevel(o2.item.level, 5)
            ) -1 else 1
        }
        return list
    }

    private fun newWifiConfiguration(result: ScanResult, password: String): WifiConfiguration {
        val configuration = WifiConfiguration()
        if (AndroidSystem.isUsePassWifi(result)) {
            configuration.SSID = "\"" + result.SSID + "\""
            configuration.preSharedKey = "\"" + password + "\""
        } else {
            configuration.SSID = "\"" + result.SSID + "\""
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        }
        return configuration
    }

    fun showNext(show: Boolean) {
        mNextView!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    private inner class WifiReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> availableAction(intent)
                WifiManager.WIFI_STATE_CHANGED_ACTION -> {}
            }
        }
    }
}
