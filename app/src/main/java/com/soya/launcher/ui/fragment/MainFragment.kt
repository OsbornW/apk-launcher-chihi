package com.soya.launcher.ui.fragment

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.NetworkUtils
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setDifferModels
import com.drake.brv.utils.setup
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.HOME_EVENT
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.REFRESH_HOME
import com.shudong.lib_base.ext.UPDATE_HOME_LIST
import com.shudong.lib_base.ext.UPDATE_WALLPAPER_EVENT
import com.shudong.lib_base.ext.animScale
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.downloadApkNopkName
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.height
import com.shudong.lib_base.ext.jsonToBean
import com.shudong.lib_base.ext.jsonToString
import com.shudong.lib_base.ext.net.lifecycle
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.width
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.App
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.BuildConfig
import com.soya.launcher.LAYOUTTYPE_HOME_GAME
import com.soya.launcher.LAYOUTTYPE_HOME_LANDSCAPE
import com.soya.launcher.R
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.AuthBean
import com.soya.launcher.bean.Data
import com.soya.launcher.bean.DivSearch
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.Movice
import com.soya.launcher.bean.MyRunnable
import com.soya.launcher.bean.Notify
import com.soya.launcher.bean.PackageName
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SearchDto
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.cache.AppCache
import com.soya.launcher.config.Config
import com.soya.launcher.databinding.FragmentMainBinding
import com.soya.launcher.databinding.HolderSetting3Binding
import com.soya.launcher.databinding.ItemHomeContentPorBinding
import com.soya.launcher.databinding.ItemHomeHeaderBinding
import com.soya.launcher.databinding.ItemHomeLocalappsBinding
import com.soya.launcher.enums.Types
import com.soya.launcher.ext.compareSizes
import com.soya.launcher.ext.convertH27002Json
import com.soya.launcher.ext.deleteAllImages
import com.soya.launcher.ext.findScreenCastApps
import com.soya.launcher.ext.getUpdateList
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.ext.isSDCard
import com.soya.launcher.ext.isShowUpdate
import com.soya.launcher.ext.isUDisk
import com.soya.launcher.ext.refresh
import com.soya.launcher.ext.refreshAppList
import com.soya.launcher.ext.silentInstallWithMutex
import com.soya.launcher.manager.FilePathMangaer
import com.soya.launcher.net.viewmodel.HomeViewModel
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.AboutActivity
import com.soya.launcher.ui.activity.LoginActivity
import com.soya.launcher.ui.activity.SearchActivity
import com.soya.launcher.ui.activity.SettingActivity
import com.soya.launcher.ui.activity.UpdateAppsActivity
import com.soya.launcher.ui.activity.UpdateLauncherActivity
import com.soya.launcher.ui.activity.WifiListActivity
import com.soya.launcher.ui.dialog.AppDialog
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.AppUtil
import com.soya.launcher.utils.isSysApp
import com.soya.launcher.view.AppLayout
import com.soya.launcher.view.MyFrameLayout
import com.soya.launcher.view.MyFrameLayoutHouse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.util.Arrays
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MainFragment : BaseWallPaperFragment<FragmentMainBinding, HomeViewModel>(),
    AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val exec = Executors.newCachedThreadPool()
    private var uiHandler: Handler? = null
    private var uuid: String? = null
    private val call: Call<*>? = null
    private var homeCall: Call<*>? = null
    private var isReqHome = false
    private var receiver: InnerReceiver? = null
    private var maxVerticalOffset = com.shudong.lib_dimen.R.dimen.qb_px_60.dimenValue()
    private val items: MutableList<TypeItem> = ArrayList()
    private val targetMenus: MutableList<TypeItem> = ArrayList()
    private var timeRunnable: MyRunnable? = null
    private var isConnectFirst = false
    private var isFullAll = false
    private var isNetworkAvailable = false
    private var requestTime = System.currentTimeMillis()
    private var isExpanded = false

    override val shouldSetPaddingTop: Boolean = false

    override fun initView() {
        uiHandler = Handler()
        receiver = InnerReceiver()

        sendLiveEventData(IS_MAIN_CANBACK, false)
    }

    override fun initObserver() {

        obseverLiveEvent<Boolean>(UPDATE_HOME_LIST) {
            val path = FilePathMangaer.getJsonPath(appContext) + "/Home.json"
            if (File(path).exists()) {
                val result = Gson().fromJson<HomeInfoDto>(
                    JsonReader(FileReader(path)),
                    HomeInfoDto::class.java
                )
                setNetData(result)
            }

        }


        obseverLiveEvent<Boolean>(HOME_EVENT) {
            it.yes {
                // true 表示处理Home事件
                backToHeadFirst()
            }.otherwise {
                // false 表示处理返回事件
                if (isExpanded) {
                    // 下面放大了
                    mBind.header.requestFocus()
                } else {
                    //上面放大了
                    backToHeadFirst()
                }
            }

        }

        obseverLiveEvent<Boolean>(UPDATE_WALLPAPER_EVENT){
            updateWallpaper()
        }

    }

    private fun backToHeadFirst() {
        (mBind.header.isFocused).no {
            mBind.header.requestFocus()
        }
        mBind.header.apply {
            if(selectedPosition!=0){
                postDelayed({
                    scrollToPosition(0)
                },0)
            }

        }
    }

    override fun initdata() {
        product.addHeaderItem()?.let { items.addAll(it) }

        //receiver?.let { mViewModel.addAppStatusBroadcast(it) }

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addDataScheme("package")
       requireContext().registerReceiver(receiver, filter)


        detectNetStaus()

        startRepeatingTask()

        checkLauncherUpdate()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) { // 当生命周期至少为 RESUMED 时执行
                repeat(20){
                    delay(500)
                    if (AppCache.isReload ) {
                        setDefault()
                        AppCache.isReload = false
                        return@repeatOnLifecycle
                    }
                }

            }
        }

        mBind.header.requestFocus()

        mBind.header.pivotY = maxVerticalOffset.toFloat()

        initHeader()
        initContentForHorizontal()
        initContentForVertical()

        mBind.hdmi.visibility =
            if (Config.COMPANY == 0 || Config.COMPANY == 9) View.VISIBLE else View.GONE
        mBind.gradient.visibility =
            if (Config.COMPANY == 0 || Config.COMPANY == 9) View.VISIBLE else View.GONE

        AppCacheBase.isActive.yes {
            mViewModel.reqCheckActiveCode(AppCacheBase.activeCode)
                .lifecycle(this@MainFragment, {
                }, isShowError = false) {
                    val authDto = this.jsonToBean<AuthBean>()
                    (authDto.status == 200).yes {
                        authDto.code.let {
                            BuildConfig.DEBUG.no { mViewModel.handleActiveCodeData(it, authDto.msg) }
                        }
                    }
                }

        }


        fillHeader()

        mBind.notifyRecycler.linear(RecyclerView.HORIZONTAL).setup {
            addType<Notify>(R.layout.holder_notify_2)
        }.models = arrayListOf()

    }

    private fun initContentForVertical() {
        mBind.verticalContent.setup {
            addType<Data> {
                when (layoutType) {
                    LAYOUTTYPE_HOME_LANDSCAPE -> R.layout.item_home_content_lan
                    LAYOUTTYPE_HOME_GAME -> R.layout.item_home_content_game
                    else -> -1
                }
            }
            addType<SettingItem>(R.layout.holder_setting_3)
            onBind {

                when (_data) {
                    is Data -> {
                        val dto = getModel<Data>()
                        val flRoot = findView<MyFrameLayoutHouse>(R.id.fl_root)
                        flRoot.apply {
                            setCallback {
                                if (it) {
                                    setRVHeight(false)
                                    setExpanded(false)
                                }
                            }
                            clickNoRepeat {
                                mViewModel.handleContentClick(dto) {
                                    if (!it) {
                                        toastInstallPKApp(dto.appName ?: "", dto.packageNames)
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        val binding = getBinding<HolderSetting3Binding>()
                        val dto = getModel<SettingItem>()
                        binding.rlRoot.apply {
                            setOnFocusChangeListener { view, b ->
                                view.animScale(b, 1.15f)
                                b.yes {
                                    binding.title.isSelected = true
                                }.otherwise {
                                    binding.title.isSelected = false
                                }
                                if (b) {
                                    setRVHeight(false)
                                    setExpanded(false)
                                }
                            }

                            clickNoRepeat {
                                mViewModel.clickProjectorItem(dto) {
                                    val type = it.first
                                    val text = it.second
                                    when (type) {
                                        Projector.TYPE_AUTO_RESPONSE -> {
                                            val tvName =
                                                mBind.verticalContent.getChildAt(0)
                                                    .findViewById<TextView>(R.id.title)
                                            tvName.text = text.toString()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }
        }.models = arrayListOf()
    }

    private fun initContentForHorizontal() {
        mBind.horizontalContent.setup {
            addType<Data>(R.layout.item_home_content_por)
            addType<ApplicationInfo>(R.layout.item_home_localapps)
            addType<AppItem>(R.layout.holder_app_store)
            onBind {
                when (_data) {
                    is Data -> {
                        val binding = getBinding<ItemHomeContentPorBinding>()
                        val dto = getModel<Data>()
                        binding.flRoot.apply {
                            setCallback {
                                if (it) {
                                    setRVHeight(false)
                                    setExpanded(false)
                                }
                            }
                            clickNoRepeat {
                                mViewModel.handleContentClick(dto) {
                                    if (!it) {
                                        toastInstallPKApp(dto.appName ?: "", dto.packageNames)
                                    }
                                }
                            }
                        }
                    }

                    is ApplicationInfo -> {
                        val dto = getModel<ApplicationInfo>()
                        val binding = getBinding<ItemHomeLocalappsBinding>()
                        binding.root.setCallback {
                            if (it) {
                                setRVHeight(false)
                                setExpanded(false)
                            }
                        }
                        binding.root.setListener(AppLayout.EventListener { keyCode, event ->
                            if (event?.keyCode == KeyEvent.KEYCODE_MENU) {
                                isSysApp(dto.packageName).no {

                                    val dialog = AppDialog.newInstance(dto)
                                    dialog.setCallback(object : AppDialog.Callback {
                                        override fun onOpen() {
                                            currentActivity?.let {
                                                AndroidSystem.openPackageName(
                                                    it,
                                                    dto.packageName
                                                )
                                            }
                                        }

                                    })
                                    dialog.show(childFragmentManager, AppDialog.TAG)
                                }
                                return@EventListener false
                            }
                            true
                        })
                    }

                    is AppItem -> {
                        val data = getModel<AppItem>()
                        (itemView as MyFrameLayout).setCallback {
                            it.yes {
                                setRVHeight(false)
                                setExpanded(false)
                            }
                        }
                        itemView.clickNoRepeat {
                            if (!TextUtils.isEmpty(data.appDownLink)) AndroidSystem.jumpAppStore(
                                requireContext(),
                                Gson().toJson(data),
                                null
                            )
                        }
                    }
                }


            }
        }.models = arrayListOf()
    }

    private fun initHeader() {
        mBind.header.setup {
            addType<TypeItem>(R.layout.item_home_header)
            onBind {
                val binding = getBinding<ItemHomeHeaderBinding>()
                val dto = getModel<TypeItem>()
                binding.root.setCallback {
                    if (it) {
                        setRVHeight(true)
                        setExpanded(true)
                        try {
                            call?.cancel()
                            uuid = UUID.randomUUID().toString()
                            work(uuid!!, dto)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                itemView.clickNoRepeat {
                    mViewModel.clickHeaderItem(dto) { name, packages ->
                        (name == null).yes {
                            toastInstall()
                        }.otherwise {
                            toastInstallPKApp(dto.name, packages)
                        }
                    }
                }
            }
        }.models = arrayListOf()
    }


    private fun checkLauncherUpdate() {
        lifecycleScope.launch {
            delay(3000)
            mViewModel.reqLauncherVersionInfo()
                .lifecycle(this@MainFragment, {}, isShowError = false) {
                    val dto = this
                    dto.downLink.isNullOrEmpty().no {
                        AppCache.updateInfoForLauncher = dto.jsonToString()
                        (dto.tip == 1).yes {
                            //强制更新
                            dto.downLink?.downloadApkNopkName(this@launch, downloadError = {

                            }, downloadComplete = { str, destPath ->
                                lifecycleScope.launch {
                                    destPath.silentInstallWithMutex()
                                }

                            }) { progress: Int ->

                            }
                        }.otherwise {
                            //非强制更新
                            startKtxActivity<UpdateLauncherActivity>()
                        }
                    }
                }

        }
    }


    private fun startRepeatingTask() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) { // 当生命周期至少为 RESUMED 时执行
                while (true) {
                    delay(3000)
                    isShowUpdate().yes { performTask() }

                }
            }
        }
    }

    private fun setNetData(result: HomeInfoDto) {

        lifecycleScope.launch {
            "开始设置网络数据".e("zengyue3")
           // mBind.setting.requestFocus()

            val header = fillData(result)
            header.addAll(items)

            addProduct5TypeItem(header)
            //delay(1500)

            setHeader(header)
            isConnectFirst = true

            val item = header[0]
            fillMovice(item)
            //delay(500)
            mBind.header.requestFocus()
            mBind.header.apply {
                postDelayed({
                    scrollToPosition(0)
                },0)
            }
            //mBind.setting.clearFocus()
            //mBind.header.requestFocus()
        }

    }


    private fun performTask() {
        mViewModel.reqUpdateInfo().lifecycle(this@MainFragment,
            errorCallback = { throwanle ->

            },
            isShowError = false,
            callback = {

                AppCache.updateInfo = this.jsonToString()
                val isHasUpdate = getUpdateList()
                isHasUpdate.yes {
                    if (currentActivity != null && currentActivity !is UpdateAppsActivity) {
                        startKtxActivity<UpdateAppsActivity>()
                    }

                }.otherwise {
                    AppCache.updateInteval = "hour"
                    AppCache.lastTipTime = System.currentTimeMillis()
                }

            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        call?.cancel()
        if (homeCall != null) homeCall!!.cancel()
        //receiver?.let { mViewModel.removeAppStatusBroadcast(it) }
        requireContext().unregisterReceiver(receiver)
        exec.shutdownNow()
    }

    override fun onPause() {
        super.onPause()
        stopLoopTime()
    }

    override fun onStop() {
        super.onStop()
        stopLoopTime()
    }

    override fun onResume() {
        super.onResume()
        syncTime()
        syncNotify()
        startLoopTime()

        (mBind.horizontalContent.mutable.size > 0).yes {
            val areAllApplicationInfo =
                mBind.horizontalContent.mutable.all { it is ApplicationInfo }
            areAllApplicationInfo.yes {
                var infos = isAppStoreSelect.yes { AndroidSystem.getUserApps2(appContext) }.otherwise {
                    findScreenCastApps()
                }
                val filteredList =
                    infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos

                val oldList = mBind.horizontalContent.mutable as MutableList<ApplicationInfo>

                if (filteredList.size != oldList.size) {
                    mBind.horizontalContent.bindingAdapter.refreshAppList(
                        oldList,
                        filteredList.toMutableList()
                    )
                }
            }
        }


    }

    /*override fun onStart() {
        super.onStart()
        syncTime()
        syncNotify()
        startLoopTime()
    }*/


    override fun initClick() {
        initFocus()

        mBind.appBar.addOnOffsetChangedListener(this)
        mBind.setting.setOnClickListener(this)
        mBind.search.setOnClickListener(this)
        mBind.wifi.setOnClickListener(this)
        mBind.login.setOnClickListener(this)
        mBind.help.setOnClickListener(this)
        mBind.hdmi.setOnClickListener(this)
        mBind.gradient.setOnClickListener(this)

    }

    private fun initFocus() {
        mBind.apply {
            mBind.setting.let {
                it.setOnFocusChangeListener { view, b ->
                    if (b) rlSetting.isVisible = true else rlSetting.visibility = View.INVISIBLE
                }
            }

            mBind.gradient.let {
                it.setOnFocusChangeListener { view, b ->
                    if (b) projection.isVisible = true else projection.visibility = View.INVISIBLE

                }
            }

            mBind.hdmi.let {
                it.setOnFocusChangeListener { view, b ->
                    if (b) hdml.isVisible = true else hdml.visibility = View.INVISIBLE

                }
            }

            mBind.wifi.let {
                it.setOnFocusChangeListener { view, b ->
                    if (b) rlWifi.isVisible = true else rlWifi.visibility = View.INVISIBLE

                }
            }


        }
    }


    private fun setHeader(items: List<TypeItem>) {
        targetMenus.clear()
        targetMenus.addAll(items)
        items.forEachIndexed { index, typeItem ->
            typeItem.itemPosition = index
        }
        mBind.header.setDifferModels(items)

    }

    private fun setMoviceContent(
        list: MutableList<Data>?,
        direction: Int,
        columns: Int,
        layoutId: Int
    ) {
        var list = list
        if ((list?.size ?: 0) > 4) {
            mBind.verticalContent.updatePadding(top = com.shudong.lib_dimen.R.dimen.qb_px_10.dimenValue())
            // flList.height(com.shudong.lib_dimen.R.dimen.qb_px_270.dimenValue())
        } else {
            mBind.verticalContent.updatePadding(top = com.shudong.lib_dimen.R.dimen.qb_px_40.dimenValue())
            // flList.height(com.shudong.lib_dimen.R.dimen.qb_px_250.dimenValue())

        }
        when (direction) {
            1 -> {
                mBind.horizontalContent.models = list
                mBind.verticalContent.visibility = View.GONE
                mBind.horizontalContent.visibility = View.VISIBLE
            }

            0, 2 -> {
                if (list?.size ?: 0 > columns * 2) {
                    list = list?.subList(0, columns * 2)
                }

                list?.forEach {
                    it.layoutType = direction
                }

                mBind.verticalContent.models = list

                mBind.verticalContent.visibility = View.VISIBLE
                mBind.horizontalContent.visibility = View.GONE
                mBind.verticalContent.setNumColumns(columns)
                appContext.let {
                    mBind.verticalContent.setVerticalSpacing(
                        it.resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_1).toInt()
                    )
                }

            }

        }
    }

    private fun stopLoopTime() {
        if (timeRunnable != null) timeRunnable!!.interrupt()
        timeRunnable = null
    }

    private fun startLoopTime() {
        if (timeRunnable != null) return
        timeRunnable = object : MyRunnable() {
            override fun run() {
                while (!isInterrupt) {
                    SystemClock.sleep(2000)
                    syncNotify()

                    if (TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - requestTime) >= 25) {
                        requestTime = System.currentTimeMillis()
                        requestHome()
                    }
                }
            }
        }
        exec.execute(timeRunnable)
    }

    private fun detectNetStaus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { // 当生命周期至少为 STARTED 时执行
                while (true) {
                    val netType = NetworkUtils.getNetworkType()
                    when (netType) {
                        NetworkUtils.NetworkType.NETWORK_ETHERNET -> {
                            mBind.wifi.setImageResource(R.drawable.baseline_lan_100)
                        }

                        else -> {
                            withContext(Dispatchers.IO) {
                                (NetworkUtils.isConnected() && NetworkUtils.isAvailable()).yes {
                                    // 达大厦
                                    withContext(Dispatchers.Main) {
                                        mBind.wifi.setImageResource(R.drawable.baseline_wifi_100)
                                    }
                                }.otherwise {
                                    withContext(Dispatchers.Main) {
                                        mBind.wifi.setImageResource(R.drawable.baseline_wifi_off_100)

                                    }

                                }
                            }
                        }
                    }

                    delay(2000) // 每2秒更新一次
                }
            }
        }
    }

    private fun syncNotify() {
        uiHandler!!.post(Runnable {
            if (!isAdded) return@Runnable
            syncTime()
            val old = isNetworkAvailable
            isNetworkAvailable = AndroidSystem.isNetworkAvailable(appContext)
            if (isNetworkAvailable != old && isNetworkAvailable) {
                requestHome()
            }

            val notifies: MutableList<Notify> = ArrayList()
            if (Config.COMPANY == 3) {
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) notifies.add(
                    Notify(
                        R.drawable.baseline_bluetooth_100,
                        3
                    )
                )
            }

            val deviceHashMap =
                (appContext.getSystemService(Context.USB_SERVICE) as UsbManager).deviceList

            val isInsertUDisk = requireActivity().isUDisk()

            isInsertUDisk.yes {
                notifies.add(Notify(R.drawable.baseline_usb_100, 0))
            }

            if (com.soya.launcher.utils.SystemUtils.isApEnable(appContext)) notifies.add(
                Notify(
                    R.drawable.baseline_wifi_tethering_100_2,
                    2
                )
            )

            val isInsertSDCard = requireActivity().isSDCard()

            isInsertSDCard.yes {
                notifies.add(Notify(R.drawable.baseline_sd_storage_100, 1))
            }

            if (notifies.size != mBind.notifyRecycler.mutable.size) {
                mBind.notifyRecycler.bindingAdapter.refresh(
                    (mBind.notifyRecycler.mutable) as MutableList<Notify>,
                    notifies
                )
            }

        })
    }


    private fun syncTime() {
        val is24 = AppUtil.is24Display(appContext)
        val calendar = Calendar.getInstance()
        val h = calendar[if (is24) Calendar.HOUR_OF_DAY else Calendar.HOUR]
        val m = calendar[Calendar.MINUTE]
        val time = getString(R.string.hour_minute_second, h, m)
        val segment = if (calendar[Calendar.AM_PM] == 0) "AM" else "PM"
        mBind.loopSegment.visibility = if (is24) View.GONE else View.VISIBLE
        mBind.loopSegment.text = segment
        mBind.loopTime.text = time
    }

    private fun setAppContent(list: List<ApplicationInfo>) {
        mBind.horizontalContent.models = list
        mBind.verticalContent.visibility = View.GONE
        mBind.horizontalContent.visibility = View.VISIBLE
    }

    private fun setProjectorContent() {
        mBind.verticalContent.setNumColumns(product.projectorColumns())
        mBind.verticalContent.isFocusSearchDisabled = false

        mBind.verticalContent.updatePadding(top = com.shudong.lib_dimen.R.dimen.qb_px_10.dimenValue())

        mBind.verticalContent.visibility = View.VISIBLE
        mBind.horizontalContent.visibility = View.GONE
        mBind.verticalContent.models = product.addProjectorItem()
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        isExpanded = verticalOffset != 0
        val value = (1f - abs((verticalOffset / 2f).toDouble()) / maxVerticalOffset).toFloat()
        mBind.header.scaleX = value
        mBind.header.scaleY = value
    }

    override fun onClick(v: View) {
        if (v == mBind.setting) {
            if (Config.COMPANY == 4) {
                AndroidSystem.openSystemSetting(requireContext())
            } else {
                startActivity(Intent(requireContext(), SettingActivity::class.java))
            }

        } else if (v == mBind.search) {
            startActivity(Intent(activity, SearchActivity::class.java))
        } else if (v == mBind.wifi) {
            if (Config.COMPANY == 3 || Config.COMPANY == 4) {
                //startActivity(Intent(activity, WifiListActivity::class.java))
                AndroidSystem.openWifiSetting(requireContext())
            } else {
                startActivity(Intent(activity, WifiListActivity::class.java))
            }
        } else if (v == mBind.login) {
            startActivity(Intent(activity, LoginActivity::class.java))
        } else if (v == mBind.help) {
            startActivity(Intent(activity, AboutActivity::class.java))
        } else if (v == mBind.hdmi) {
            AndroidSystem.openProjectorHDMI(requireContext())
        } else if (v == mBind.gradient) {
            product.openHomeTopKeystoneCorrection(requireContext())

        }
    }

    private fun work(flag: String, bean: TypeItem) {
        uiHandler!!.postDelayed({
            if (flag == uuid) {
                selectWork(bean)
            }
        }, 220)
    }

    var isAppStoreSelect = false
    var curType = -1
    var curId = -1L
    private fun selectWork(bean: TypeItem) {
        if(curType!=bean.type||curId!==bean.id){
            when (bean.type) {
                Types.TYPE_MY_APPS -> {
                    isAppStoreSelect = true
                    fillApps(true)
                }

                Types.TYPE_APP_STORE -> {
                    fillAppStore()
                }

                Types.TYPE_ScreenCast -> {
                    isAppStoreSelect = false
                    setScreenCastData()
                }

                Types.TYPE_PROJECTOR -> {
                    setProjectorContent()
                }

                else -> switchMovice(bean)
            }
            curType = bean.type
            curId = bean.id
        }

    }

    private fun setScreenCastData() {
        findScreenCastApps().let { mBind.horizontalContent.models = it }

        mBind.verticalContent.visibility = View.GONE
        mBind.horizontalContent.visibility = View.VISIBLE
    }


    private fun toastInstall() {
        toast(getString(R.string.place_install_app), ToastDialog.MODE_DEFAULT, null)
    }

    private fun toastInstallApp(name: String, callback: ToastDialog.Callback) {
        toast(getString(R.string.place_install, name), ToastDialog.MODE_DEFAULT, callback)
    }

    private fun toast(title: String, mode: Int, callback: ToastDialog.Callback?) {
        val dialog = ToastDialog.newInstance(title, mode)
        dialog.setCallback(callback)
        dialog.show(getChildFragmentManager(), ToastDialog.TAG)
    }

    private fun fillMovice(bean: TypeItem) {
        var layoutId = R.layout.item_home_content_por
        var columns = 1
        when (bean.layoutType) {
            1 -> {
                columns = 0
                layoutId = R.layout.item_home_content_por
            }

            0 -> {
                columns = 4
                layoutId = R.layout.item_home_content_lan
            }

            2 -> {
                columns = 4
                layoutId = R.layout.item_home_content_game
            }
        }
        fillMovice(bean.id, bean.layoutType, columns, layoutId)
    }

    private fun fillMovice(id: Long, dirction: Int, columns: Int, layoutId: Int) {
        setMoviceContent(App.MOVIE_MAP[id], dirction, columns, layoutId)
    }

    private fun requestHome() {
        if (isReqHome || isConnectFirst) return
        isReqHome = true
        sendLiveEventData(REFRESH_HOME, true)
    }

    private fun fillApps(isAttach: Boolean) {

        var infos = AndroidSystem.getUserApps2(appContext)
        val filteredList = infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos

        if (isAttach) {
            setAppContent(filteredList)
        }
    }

    private fun fillAppStore() {
        if (App.APP_STORE_ITEMS.isEmpty()) {
            val emptys: MutableList<AppItem> = ArrayList()
            try {
                val apps = Gson().fromJson(
                    InputStreamReader(
                        appContext.assets.open("app.json")
                    ), Array<AppItem>::class.java
                )
                if (apps != null) {
                    emptys.addAll(Arrays.asList(*apps))
                }
                App.APP_STORE_ITEMS.addAll(Arrays.asList(*apps))
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                setStoreContent(emptys)
                mViewModel.reqSearchAppList(
                    tag = "hot", pageSize = 20)
                    .lifecycle(this@MainFragment, {
                    }) {
                        val dto = this.jsonToBean<SearchDto>()
                        //"当前获取的数据是：${dto.code}==${dto.msg}==${dto.result?.appList?.size}".e("chihi_error")
                        if((dto.result?.appList?.size ?: 0) > 0){
                            dto.result?.appList?.let { App.APP_STORE_ITEMS.addAll(it) }

                            setStoreContent(App.APP_STORE_ITEMS)
                        }

                    }

            }
        } else {
            setStoreContent(App.APP_STORE_ITEMS)
        }
        isFullAll = true
    }

    private fun setStoreContent(list: List<AppItem>) {

        mBind.horizontalContent.models = list

        mBind.verticalContent.visibility = View.GONE
        mBind.horizontalContent.visibility = View.VISIBLE
    }


    private fun fillHeader() {
        try {
            val path = FilePathMangaer.getJsonPath(appContext) + "/Home.json"
            if (File(path).exists()) {
                val result = Gson().fromJson<HomeInfoDto>(
                    JsonReader(FileReader(path)),
                    HomeInfoDto::class.java
                )
                if (compareSizes(result)) {
                    "我加载的是网络的资源".e("zengyue3")
                    val header = fillData(result)
                    header.addAll(items)
                    addProduct5TypeItem(header)

                    setHeader(header)
                    if (BuildConfig.FLAVOR == "hongxin_H27002") {
                        mBind.header.postDelayed({
                            mBind.header.requestFocus()
                        }, 500)
                    }
                } else {
                    "我加载的是默认的资源1".e("zengyue3")
                    setDefault()
                }

            } else {
                "我加载的是默认的资源2".e("zengyue3")
                setDefault()
            }
        } catch (e: Exception) {
            "我加载的是默认的资源3".e("zengyue3")
            setDefault()
        }
    }

    fun addProduct5TypeItem(header: MutableList<TypeItem>) {
        if (Config.COMPANY == 5) {
            //getString(R.string.apps)
            header.add(
                0, TypeItem(
                    getString(R.string.apps),
                    R.drawable.app_list,
                    0,
                    Types.TYPE_MY_APPS,
                    TypeItem.TYPE_ICON_IMAGE_RES,
                    TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                )
            )

            val menuList = convertH27002Json()
            header.addAll(1, menuList)
        }

    }

    private fun setDefault() {
        try {
            val result = Gson().fromJson(
                InputStreamReader(
                    appContext.assets.open("home.json")
                ),
                HomeInfoDto::class.java
            )
            val header = fillData(result)
            header.addAll(items)
            addProduct5TypeItem(header)

            product.addGameItem()?.let { header.addAll(0, it) }

            setHeader(header)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fillData(
        result: HomeInfoDto,
        iconType: Int = TypeItem.TYPE_ICON_IMAGE_URL,
        imageType: Int = 0
    ): MutableList<TypeItem> {
        val homeItems = result.movies
        val menus: MutableList<TypeItem> = ArrayList()
        val gson = Gson()
        homeItems?.let {
            for (bean in homeItems) {
                val movices: MutableList<Data> = ArrayList(bean?.datas?.size ?: 0)
                bean?.datas?.let {
                    for (movice in bean.datas) {
                        movice?.picType = Movice.PIC_NETWORD
                        movice?.packageNames = bean.packageNames
                        movice?.appName = bean.name
                        //movice.appPackage = bean.packageNames

                        if (movice != null) {
                            movices.add(movice)
                        }
                    }
                }

                val item = TypeItem(
                    bean?.name ?: "",
                    bean?.icon ?: "",
                    UUID.randomUUID().leastSignificantBits,
                    Types.TYPE_MOVICE,
                    iconType,
                    bean?.type ?: 0
                )
                item.iconName = bean?.iconName
                item.data = bean?.packageNames
                App.MOVIE_MAP.put(item.id, movices)
                if (Config.COMPANY == 5) {
                    when (item.name) {
                        "Max", "Hulu", "Google play", "media center" -> {
                        }

                        else -> {
                            menus.add(item)
                        }
                    }
                } else {
                    if (item.name == "Disney+") {
                        isRK3326().no {
                            menus.add(item)
                        }
                    } else {
                        menus.add(item)
                    }

                }

            }
        }

        return menus
    }


    fun setRVHeight(isExpanded: Boolean) {
        isExpanded.yes {
            //展开
            mBind.header.width(ViewGroup.LayoutParams.MATCH_PARENT)
        }.otherwise {
            //收缩
            mBind.header.width(5000)
            if ((mBind.header.adapter?.itemCount ?: 0) > 8) {
                if((mBind.header.adapter?.itemCount ?: 0) ==9){
                    mBind.toolbar.height(com.shudong.lib_dimen.R.dimen.qb_px_110.dimenValue())
                }else{
                    mBind.toolbar.height(com.shudong.lib_dimen.R.dimen.qb_px_105.dimenValue())
                }

            } else {
                mBind.toolbar.height(com.shudong.lib_dimen.R.dimen.qb_px_115.dimenValue())
            }
        }
    }


    private fun toastInstallPKApp(name: String, packages: List<PackageName?>?) {
        toastInstallApp(name, object : ToastDialog.Callback {
            override fun onClick(type: Int) {
                if (type == 1) {
                    if((packages?.size ?: 0) > 1){

                        lifecycleScope.launch {
                            for (pkg in packages!!) {
                                val packageName = pkg?.packageName ?: continue // 如果包名为空，跳过
                                try {
                                    // 发起请求并等待结果
                                    val result = CompletableDeferred<SearchDto>()
                                    mViewModel.reqSearchAppList(keyWords = packageName)
                                        .lifecycle(this@MainFragment, errorCallback = { throwable ->
                                            result.completeExceptionally(throwable) // 如果出错，标记失败
                                        }) {
                                            val dto = this.jsonToBean<SearchDto>()
                                            result.complete(dto) // 正常完成请求
                                        }

                                    val dto = result.await() // 等待请求结果

                                    // 判断请求结果
                                    if ((dto.result?.appList?.size ?: 0) > 0) {
                                        "找到有效结果，包名: $packageName".e("Search")
                                        AndroidSystem.jumpAppStore(requireContext(), null, packages.get(0)?.packageName)
                                        break // 退出循环
                                    } else {
                                        "无效结果，包名: $packageName".e("Search")
                                    }
                                } catch (e: Exception) {
                                    // 捕获异常，继续处理下一个包
                                    "请求失败，包名: $packageName, 异常: ${e.message}".e("Search")
                                }
                            }
                        }

                    }else AndroidSystem.jumpAppStore(requireContext(), null, packages?.get(0)?.packageName)

                }
            }

        })
    }

    private fun deleteAllPic() {
        appContext.filesDir.absolutePath.deleteAllImages()
    }

    private fun switchMovice(bean: TypeItem) {
        if ((App.MOVIE_MAP[bean.id]?.size ?: 0) > 0) {
            fillMovice(bean)
        }
    }



    private fun setExpanded(isExpanded: Boolean) {
        mBind.appBar.setExpanded(isExpanded)
    }


    inner class InnerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_REPLACED -> {

                    val areAllApplicationInfo =
                        mBind.horizontalContent.mutable.all { it is ApplicationInfo }
                    areAllApplicationInfo.yes {
                        var infos = isAppStoreSelect.yes { AndroidSystem.getUserApps2(appContext) }.otherwise {
                            findScreenCastApps()
                        }
                        val filteredList =
                            infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos

                        val oldList =
                            mBind.horizontalContent.mutable as MutableList<ApplicationInfo>

                        if (filteredList.size != oldList.size) {
                            mBind.horizontalContent.bindingAdapter.refreshAppList(
                                mBind.horizontalContent.mutable as MutableList<ApplicationInfo>,
                                filteredList.toMutableList()
                            )
                        }

                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): MainFragment {
            val args = Bundle()
            val fragment = MainFragment()
            fragment.setArguments(args)
            return fragment
        }
    }
}
