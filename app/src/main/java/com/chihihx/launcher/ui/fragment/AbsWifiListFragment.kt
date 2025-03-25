package com.chihihx.launcher.ui.fragment

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
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.NetworkUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.stringValue
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.BaseWallPaperFragment
import com.chihihx.launcher.R
import com.chihihx.launcher.adapter.WifiListAdapter
import com.chihihx.launcher.bean.WifiItem
import com.chihihx.launcher.databinding.FragmentWifiListBinding
import com.chihihx.launcher.ext.isGreaterThanLollipop
import com.chihihx.launcher.ext.navigateTo
import com.chihihx.launcher.pop.showWifiPWDDialog
import com.chihihx.launcher.pop.showWifiSavedDialog
import com.chihihx.launcher.ui.dialog.ProgressDialog
import com.chihihx.launcher.ui.dialog.ToastDialog
import com.chihihx.launcher.utils.AndroidSystem
import com.chihihx.launcher.utils.toTrim
import com.chihihx.launcher.view.MyFrameLayout
import com.thumbsupec.lib_base.toast.ToastUtils
import kotlinx.coroutines.launch
import repeatWithDelay
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


open class AbsWifiListFragment<VDB : FragmentWifiListBinding, VM : BaseViewModel> :
    BaseWallPaperFragment<VDB, VM>() {
    private var exec = Executors.newCachedThreadPool()

    private var mWifiManager: WifiManager? = null
    private var mAdapter: WifiListAdapter? = null
    private var receiver: WifiReceiver? = null
    private var mDialog: ProgressDialog? = null
    private var uiHandler: Handler? = null
    private var mTarget: ScanResult? = null
    private var connectedTime: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mWifiManager = requireActivity().getSystemService(WifiManager::class.java)
        } else {
            mWifiManager = requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager?
        }
        mDialog = ProgressDialog.newInstance()
        uiHandler = Handler()
        receiver = WifiReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        requireActivity().registerReceiver(receiver, intentFilter)

        lifecycleScope.launch {
            repeatWithDelay(Int.MAX_VALUE, 1800) {
                val netType = NetworkUtils.getNetworkType()
                (netType == NetworkUtils.NetworkType.NETWORK_ETHERNET).yes {
                    //以太网
                    isEthrnet = true
                }.otherwise {
                    //wifi
                    isEthrnet = false
                }

            }
        }
    }

    var isEthrnet = false

    override fun onDestroy() {
        super.onDestroy()
        exec.shutdownNow()
        requireActivity().unregisterReceiver(receiver)
    }


    fun setRVSaveHeight(height: Int) {
        mBind.apply {
            mBind.rvSavedWifi.post {
                mBind.rvSavedWifi.layoutParams?.height = height
                mBind.rvSavedWifi.requestLayout() // 强制重新布局
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun initView() {
        mBind.tvNet1.text = R.string.connected_to_net.stringValue()
        mBind.tvNet2.text = R.string.ip_address.stringValue()
        mBind.tvNet3.text = R.string.signal_strength.stringValue()
        mAdapter =
            WifiListAdapter(
                requireActivity(),
                LayoutInflater.from(appContext),
                CopyOnWriteArrayList(),
                useNext(),
                newCallback()
            )
        mAdapter!!.replace(fillterWifi(mWifiManager!!.scanResults))


        mBind.wifiConnected.let {
            it.clickNoRepeat {

                val info = mWifiManager!!.connectionInfo

                val title = R.string.connected_wifi_tip.stringValue()
                val rightText = R.string.disconnect_net.stringValue()
                val middleText = R.string.unsave.stringValue()
                showWifiSavedDialog {
                    textTitleData.value = title
                    textContentData.value = info.ssid
                    textConfirmData.value = rightText
                    textCleanData.value = middleText
                    confirmAction {
                        mWifiManager?.disableNetwork(info!!.networkId)
                    }

                    cleanAction {
                        mWifiManager!!.removeNetwork(info!!.networkId)
                    }

                }

            }
        }

        /*
        * wifi是否关闭状态
        * */
        mWifiManager?.let {
            (it.isWifiEnabled).yes {
                mBind.off.isVisible = false
                mBind.content.isVisible = true
                mBind.rvSavedWifi.isVisible = true
                mBind.wifiConnected.isVisible = true
            }.otherwise {
                mBind.off.isVisible = true
                mBind.content.isVisible = false
                mBind.rvSavedWifi.isVisible = false
                mBind.wifiConnected.isVisible = false
            }
        }


        // 已保存WiFi列表item 点击回调
        var myCallback: WifiListAdapter.Callback = savedCallback()


        /**
         * 已保存WiFi列表 控件初始化和数据绑定
         */
        mBind.rvSavedWifi.linear()
            .setup {


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
            }.models = arrayListOf()


        //列表加载进度条是否可见
        (mWifiManager?.isWifiEnabled ?: true).yes {
            mBind.progressBar.visibility = if (mAdapter!!.itemCount == 0) View.VISIBLE
            else View.GONE
        }

        if (useNext()) {
            mBind.wifiEnable.nextFocusLeftId = R.id.next
            mBind.wifiEnable.nextFocusRightId = R.id.next
        }

        mBind.wifiEnable.setOnClickListener {
            val isEnable = !mWifiManager!!.isWifiEnabled
            mWifiManager!!.setWifiEnabled(isEnable)


            mBind.switchItem.isChecked = isEnable

            (isEnable && mAdapter!!.itemCount == 0).yes {
                mBind.progressBar.isVisible = true
            }.otherwise {
                mBind.progressBar.isVisible = false
            }

            (isEnable).yes {
                mBind.off.isVisible = false
                mBind.content.isVisible = true
                mBind.rvSavedWifi.isVisible = true
                mBind.wifiConnected.isVisible = true
            }.otherwise {
                mBind.off.isVisible = true
                mBind.content.isVisible = false
                mBind.rvSavedWifi.isVisible = false
                mBind.wifiConnected.isVisible = false
            }

        }
        mBind.next.setOnClickListener {
            requireActivity().supportFragmentManager.navigateTo(
                R.id.main_browse_fragment,
                ChooseWallpaperFragment.newInstance()
            )

        }
        mDialog!!.setCallback(object : ProgressDialog.Callback {
            override fun onDismiss() {
                connectedTime = -1
            }

        })

    }

    override fun initdata() {
        mBind.content.setAdapter(mAdapter)
        mBind.switchItem.isChecked = mWifiManager!!.isWifiEnabled
        if (exec.isShutdown) exec = Executors.newCachedThreadPool()
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
                    if (!TextUtils.isEmpty(info.ssid) && info.ipAddress != 0 && info.ssid != "<unknown ssid>" && info.ssid != "0x") {

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
        mBind.wifiEnable.requestFocus()
    }


    private fun toastFail() {
        if (!isAdded) return
        val netType = NetworkUtils.getNetworkType()
        if (netType == NetworkUtils.NetworkType.NETWORK_WIFI && NetworkUtils.isConnected()) {
            return
        }
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

        mBind.ip.text = Formatter.formatIpAddress(info.ipAddress)
        mBind.signal.text = if (isConnected) levelStr else "-"
        mBind.networdConnectedMes.text =
            if (isConnected) getString(R.string.connected) else R.string.connecting.stringValue()
        mBind.tvConnectedStatus.text =
            if (isConnected) getString(R.string.connected) else R.string.connecting.stringValue()

        (info.ssid.contains("unknown") || info.ssid.contains("0x")).yes {
            mBind.wifiName.text = ""
            mBind.wifiConnected.isVisible = false
        }.otherwise {
            mBind.wifiConnected.isVisible = true
            mBind.wifiName.text = getNoDoubleQuotationSSID(info.ssid)
            mBind.tvConnectedWifiname.text = getNoDoubleQuotationSSID(info.ssid)
        }

        var isNeedPwd = true

        items?.let {
            it.forEach { wifiItem ->
                if (wifiItem.item.SSID == getNoDoubleQuotationSSID(info.ssid)) {
                    isNeedPwd = AndroidSystem.isUsePassWifi(wifiItem.item)
                }
            }
        }

        isNeedPwd.yes {
            mBind.ivOck.isVisible = true
        }.otherwise {
            mBind.ivOck.isVisible = false
        }


        /**
         * 已保存列表中的WiFi连接成功后，从已保存列表中进行移除
         * */
        isConnected.yes {
            ((mBind.rvSavedWifi.mutable as MutableList<WifiItem>).size > 0).yes {
                for (i in (mBind.rvSavedWifi.mutable as MutableList<WifiItem>).size - 1 downTo 0) {

                    if ((mBind.rvSavedWifi.mutable as MutableList<WifiItem>)[i].item.SSID == mAdapter!!.connectSSID) {
                        (mBind.rvSavedWifi.mutable as MutableList<WifiItem>).removeAt(i)
                        mBind.rvSavedWifi.adapter?.notifyDataSetChanged()

                        mBind.wifiConnected.post {
                            mBind.wifiConnected.requestFocus()

                        }
                    }
                }

                ((mBind.rvSavedWifi.mutable as MutableList<WifiItem>).size > 2).yes {
                    setRVSaveHeight(com.shudong.lib_dimen.R.dimen.qb_px_120.dimenValue())
                }.otherwise {
                    setRVSaveHeight(RecyclerView.LayoutParams.WRAP_CONTENT)
                }

            }
        }

        // 信号状态更新
        if (level == 1 || level == 2) {
            mBind.tvConnectedSignal.setImageResource(R.drawable.baseline_wifi_1_bar_100)
        } else if (level == 3) {
            mBind.tvConnectedSignal.setImageResource(R.drawable.baseline_wifi_2_bar_100)
        } else {
            mBind.tvConnectedSignal.setImageResource(R.drawable.baseline_wifi_100)
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

                val netType = NetworkUtils.getNetworkType()
                (netType == NetworkUtils.NetworkType.NETWORK_ETHERNET).yes {
                    //以太网
                    ToastUtils.show(R.string.ethernet)
                    return
                }

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

                    val title = R.string.saved_wifi_tip.stringValue()
                    val rightText = R.string.connect.stringValue()
                    val middleText = R.string.unsave.stringValue()
                    showWifiSavedDialog {
                        textTitleData.value = title
                        textContentData.value = bean.SSID
                        textConfirmData.value = rightText
                        textCleanData.value = middleText
                        confirmAction {
                            val index = mAdapter!!.getDataList().indexOf(wifiItem)
                            isGreaterThanLollipop().yes {
                                connect(bean, item!!.networkId)
                            }.otherwise {
                                context?.let { connectTo21_netId(it, bean, item!!.networkId) }
                            }

                            wifiItem.isSave = true
                            if (index != -1) mAdapter!!.notifyItemChanged(index)
                        }

                        cleanAction {
                            val index = mAdapter!!.getDataList().indexOf(wifiItem)
                            mWifiManager!!.removeNetwork(item!!.networkId)
                            wifiItem.isSave = false
                            if (index != -1) mAdapter!!.notifyItemChanged(index)
                        }

                    }


                } else if (usePass) {
                    showWifiPWDDialog {
                        textTitleData.value = appContext.getString(
                            R.string.wifi_password_prompt,
                            wifiItem.item.SSID
                        )
                        confirmAction {
                            wifiItem.isSave = true
                            val index = mAdapter!!.getDataList().indexOf(wifiItem)
                            if (index != -1) {
                                mAdapter?.getDataList()?.removeAt(index)
                                mAdapter?.notifyItemRemoved(index)
                            }
                            isGreaterThanLollipop().yes {
                                connect(bean, it)
                            }.otherwise {
                                context?.let { context -> connectTo_21(context, bean, it) }
                            }
                        }
                    }


                } else {
                    wifiItem.isSave = true
                    val index = mAdapter!!.getDataList().indexOf(wifiItem)
                    if (index != -1) mAdapter!!.notifyItemChanged(index)
                    isGreaterThanLollipop().yes {
                        connect(bean, "")
                    }.otherwise {
                        context?.let { connectTo_21(it, bean, "") }
                    }
                }
            }
        }
    }

    private fun savedCallback(): WifiListAdapter.Callback {
        return object : WifiListAdapter.Callback {
            @SuppressLint("MissingPermission")
            override fun onClick(wifiItem: WifiItem?) {

                val netType = NetworkUtils.getNetworkType()
                (netType == NetworkUtils.NetworkType.NETWORK_ETHERNET).yes {
                    //以太网
                    ToastUtils.show(R.string.ethernet)
                    return
                }

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


                    val title = R.string.saved_wifi_tip.stringValue()
                    val rightText = R.string.connect.stringValue()
                    val middleText = R.string.unsave.stringValue()
                    showWifiSavedDialog {
                        textTitleData.value = title
                        textContentData.value = bean.SSID
                        textConfirmData.value = rightText
                        textCleanData.value = middleText
                        confirmAction {
                            val index = mBind.rvSavedWifi.mutable.indexOf(wifiItem)
                            isGreaterThanLollipop().yes {
                                connect(bean, item!!.networkId)
                            }.otherwise {
                                context?.let { connectTo21_netId(it, bean, item!!.networkId) }
                            }
                            wifiItem.isSave = true
                            if (index != -1) mAdapter!!.notifyItemChanged(index)
                        }

                        cleanAction {
                            val index = mAdapter!!.getDataList().indexOf(wifiItem)
                            mWifiManager!!.removeNetwork(item!!.networkId)
                            wifiItem.isSave = false
                            if (index != -1) {
                                mBind.rvSavedWifi.mutable.removeAt(index)
                                mBind.rvSavedWifi.adapter?.notifyItemRemoved(index)
                            }

                            setRVSaveHeight(RecyclerView.LayoutParams.WRAP_CONTENT)
                        }

                    }


                } else if (usePass) {
                    showWifiPWDDialog {
                        textTitleData.value = appContext.getString(
                            R.string.wifi_password_prompt,
                            wifiItem.item.SSID
                        )
                        confirmAction {
                            wifiItem.isSave = true
                            val index = mBind.rvSavedWifi.mutable.indexOf(wifiItem)
                            if (index != -1) mBind.rvSavedWifi.adapter!!.notifyItemChanged(index)
                            isGreaterThanLollipop().yes {
                                connect(bean, it)
                            }.otherwise {
                                context?.let { context -> connectTo_21(context, bean, it) }
                            }
                        }
                    }


                } else {
                    wifiItem.isSave = true
                    val index = mBind.rvSavedWifi.mutable.indexOf(wifiItem)
                    if (index != -1) mBind.rvSavedWifi.adapter!!.notifyItemChanged(index)
                    isGreaterThanLollipop().yes {
                        connect(bean, "")
                    }.otherwise {
                        context?.let { connectTo_21(it, bean, "") }
                    }
                }
            }
        }
    }

    private fun showDialog() {
        mBind.loop.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(activity, R.anim.loop_translate)
        mBind.loop.startAnimation(animation)
        //mDialog.show(getChildFragmentManager(), ProgressDialog.TAG);
    }

    private fun closeDialog() {
        mBind.loop.visibility = View.GONE
        if (mBind.loop.animation != null) mBind.loop.animation.cancel()
        //if (mDialog.isAdded() && mDialog.isVisible()) mDialog.dismiss();
    }

    private fun connect(bean: ScanResult, networkId: Int) {
        target(bean)
        mWifiManager!!.enableNetwork(networkId, true)
        showDialog()
    }


    fun connectTo21_netId(context: Context, bean: ScanResult, networkId: Int) {

        target(bean)

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // 断开当前连接（如果已连接）
        wifiManager.disconnect()

        // 启用指定网络并连接
        wifiManager.enableNetwork(networkId, true)
        wifiManager.reconnect()

        showDialog()

    }


    fun connectTo_21(context: Context, scanResult: ScanResult, password: String) {
        "当前密码 的长度是$password====${password.length}"
        val pwd = password.toTrim()

        target(scanResult)

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // 断开当前连接（如果已连接）
        wifiManager.disconnect()

        // 创建 WifiConfiguration
        val config = WifiConfiguration()
        config.SSID = "\"${scanResult.SSID}\"" // SSID 需要用双引号括起来

        if (pwd.isEmpty()) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        } else {
            config.preSharedKey = "\"${pwd}\"" // 密码也需要用双引号括起来
        }
        //config.preSharedKey = "\"${pwd}\"" // 密码也需要用双引号括起来

        // 添加网络并连接
        val netId = wifiManager.addNetwork(config)
        if (netId != -1) {
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
        } else {
            // 处理添加网络失败的情况
            // ...
            ToastUtils.show("网络添加失败")
        }

        showDialog()
    }

    private fun connect(bean: ScanResult, password: String) {
        val pwd = password.toTrim()
        target(bean)
        val configuration = newWifiConfiguration(bean, pwd)
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

    var items: MutableList<WifiItem>? = null

    @SuppressLint("MissingPermission")
    private fun availableAction(intent: Intent) {

        val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
        val results = mWifiManager!!.scanResults
        items = fillterWifi(results)

        items?.let {


            it.forEach {
                //"所有的WiFi数据======${it.item.SSID}===是否保存==${it.isSave}"
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
            val cur = mBind.rvSavedWifi.mutable as MutableList<WifiItem>

            //mBind.rvSavedWifi?.mutable?.clear()
            val repeat = it.distinctBy { it.item.SSID }

            val filteredList = repeat.filterNot { it in cur }

            filteredList.forEach {
                it.isSave.yes {
                    //"当前过滤后的 集合是=====${it.item.SSID}"
                }

            }


            filteredList.forEach {

                it.isSave.yes {
                    // "当前即将要添加的已保存的WIFI数据是===${wifiItem.item.SSID}"
                    val saveList = (mBind.rvSavedWifi.mutable as MutableList<WifiItem>)
                    var isExist = false
                    saveList.forEach { wifiItem ->
                        if (it.item.SSID == wifiItem.item.SSID) {
                            isExist = true
                        }
                    }
                    if (!isExist) {
                        mBind.rvSavedWifi.mutable.add(it)
                        mBind.rvSavedWifi.adapter?.notifyItemInserted(
                            mBind.rvSavedWifi.mutable.size
                        )
                    }
                    //"当前集合里面是否包含这个WIFI？${it?.item?.SSID}===${isContain}"

                }


            }


            for (i in (mBind.rvSavedWifi.mutable as MutableList<WifiItem>).size - 1 downTo 0) {

                if ((mBind.rvSavedWifi.mutable as MutableList<WifiItem>)[i].item.SSID == mAdapter!!.connectSSID
                ) {
                    (mBind.rvSavedWifi.mutable as MutableList<WifiItem>).removeAt(i)
                    mBind.rvSavedWifi.adapter?.notifyItemRemoved(i)

                }
            }


            ((mBind.rvSavedWifi.mutable as MutableList<WifiItem>).size > 2).yes {
                setRVSaveHeight(com.shudong.lib_dimen.R.dimen.qb_px_120.dimenValue())
            }.otherwise {
                setRVSaveHeight(RecyclerView.LayoutParams.WRAP_CONTENT)
            }


            mAdapter?.let {
                for (i in (it.getDataList()).size - 1 downTo 0) {
                    if ((it.getDataList())[i].item.SSID == mAdapter?.connectSSID) {
                        (it.getDataList()).removeAt(i)
                        mAdapter?.notifyItemRemoved(i)
                    }
                    mBind.rvSavedWifi.let { it1 ->
                        for (j in (it1.mutable).size - 1 downTo 0) {
                            try {
                                if ((it.getDataList())[i].item.SSID == ((it1.mutable)[j] as WifiItem).item.SSID) {
                                    (it.getDataList()).removeAt(i)
                                    mAdapter?.notifyItemRemoved(i)
                                }
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                e.printStackTrace()
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
                    //"当前要未添加和保存WiFi是=====${it.item.SSID}"
                }
            }

            mBind.progressBar.visibility =
                if (mAdapter!!.itemCount == 0 && mBind.off.visibility == View.GONE) View.VISIBLE else View.GONE

        }
    }

    private var c: MutableList<WifiItem> = arrayListOf()
    private var d: MutableList<WifiItem> = arrayListOf()

    @SuppressLint("MissingPermission")
    private fun fillterWifi(results: MutableList<ScanResult>): MutableList<WifiItem> {
        val list: MutableList<WifiItem> = mutableListOf()
        val saves = mWifiManager?.configuredNetworks
        saves?.forEach {
            //"当前的已保存的WIFI数据是===${it.SSID}"
        }
        val map: MutableMap<String, WifiConfiguration> = HashMap()
        if (saves != null) {
            for (item in saves) map[cleanSSID(item.SSID)] = item
        }
        for (result in results) {
            if (!TextUtils.isEmpty(result.SSID)) {
                val item = map[result.SSID]
                val isSave = item != null
                list.add(WifiItem(result, isSave))
            }
        }
        try {
            list.sortWith { o1, o2 ->
                if (WifiManager.calculateSignalLevel(
                        o1.item.level,
                        5
                    ) > WifiManager.calculateSignalLevel(o2.item.level, 5)
                ) -1 else 1
            }
        } catch (e: Exception) {
            "异常是====${e.message}"
            e.printStackTrace()
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
        mBind.next.visibility = if (show) View.VISIBLE else View.GONE
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
