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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.databinding.ViewDataBinding
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.NetworkUtils
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.HOME_EVENT
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.REFRESH_HOME
import com.shudong.lib_base.ext.UPDATE_HOME_LIST
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
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.App
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.BuildConfig
import com.soya.launcher.R
import com.soya.launcher.adapter.AppListAdapter
import com.soya.launcher.adapter.MainContentAdapter
import com.soya.launcher.adapter.MainHeaderAdapter
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.AuthBean
import com.soya.launcher.bean.Data
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.Movice
import com.soya.launcher.bean.MyRunnable
import com.soya.launcher.bean.Notify
import com.soya.launcher.bean.PackageName
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.cache.AppCache
import com.soya.launcher.callback.ContentCallback
import com.soya.launcher.callback.HeaderCallback
import com.soya.launcher.config.Config
import com.soya.launcher.databinding.FragmentMainBinding
import com.soya.launcher.decoration.HSlideMarginDecoration
import com.soya.launcher.enums.Atts
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.enums.Tools
import com.soya.launcher.enums.Types
import com.soya.launcher.ext.compareSizes
import com.soya.launcher.ext.convertH27002Json
import com.soya.launcher.ext.deleteAllImages
import com.soya.launcher.ext.getUpdateList
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.ext.isSDCard
import com.soya.launcher.ext.isShowUpdate
import com.soya.launcher.ext.isUDisk
import com.soya.launcher.http.AppServiceRequest
import com.soya.launcher.http.HttpRequest
import com.soya.launcher.http.HttpRequest.checkVersion
import com.soya.launcher.http.ServiceRequest
import com.soya.launcher.http.response.AppListResponse
import com.soya.launcher.http.response.VersionResponse
import com.soya.launcher.manager.FilePathMangaer
import com.soya.launcher.ui.activity.AboutActivity
import com.soya.launcher.ui.activity.AppsActivity
import com.soya.launcher.ui.activity.ChooseGradientActivity
import com.soya.launcher.ui.activity.LoginActivity
import com.soya.launcher.ui.activity.MainActivity
import com.soya.launcher.ui.activity.ScaleScreenActivity
import com.soya.launcher.ui.activity.SearchActivity
import com.soya.launcher.ui.activity.SettingActivity
import com.soya.launcher.ui.activity.WifiListActivity
import com.soya.launcher.ui.dialog.AppDialog
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.AppUtil
import com.soya.launcher.utils.PreferencesUtils
import com.soya.launcher.utils.md5
import com.shudong.lib_base.ext.loading.showLoadingViewDismiss
import com.shudong.lib_base.ext.net.lifecycleLoadingView
import com.shudong.lib_base.ext.width
import com.soya.launcher.callback.SelectedCallback
import com.soya.launcher.databinding.HolderAppStoreBinding
import com.soya.launcher.databinding.ItemHomeContentLanBinding
import com.soya.launcher.databinding.ItemHomeContentPorBinding
import com.soya.launcher.databinding.ItemHomeHeaderBinding
import com.soya.launcher.databinding.ItemHomeLocalappsBinding
import com.soya.launcher.ext.refresh
import com.soya.launcher.ext.refreshAppList
import com.soya.launcher.ext.silentInstallWithMutex
import com.soya.launcher.net.viewmodel.HomeViewModel
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.UpdateAppsActivity
import com.soya.launcher.ui.activity.UpdateLauncherActivity
import com.soya.launcher.utils.isSysApp
import com.soya.launcher.view.AppLayout
import com.soya.launcher.view.MyFrameLayout
import com.soya.launcher.view.MyFrameLayoutHouse
import com.soya.launcher.view.RelativeLayoutHouse
import com.thumbsupec.lib_base.toast.ToastUtils
import dalvik.system.ZipPathValidator.setCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.util.Arrays
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MainFragment : BaseWallPaperFragment<FragmentMainBinding, HomeViewModel>(),
    AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private val useApps: MutableList<ApplicationInfo> = ArrayList()
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
    private var mMainHeaderAdapter: MainHeaderAdapter? = null
    private var mHMainContentAdapter: MainContentAdapter? = null
    private var mVMainContentAdapter: MainContentAdapter? = null
    private var mAppListAdapter: AppListAdapter? = null
    private var requestTime = System.currentTimeMillis()
    private var isExpanded = false


    override fun initView() {
        uiHandler = Handler()
        receiver = InnerReceiver()
        val infos = AndroidSystem.getUserApps2(appContext)
        val filteredList = infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos

        useApps.addAll(filteredList)

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
            if (isExpanded) {
                mBind.header.requestFocus()
                mBind.header.scrollToPosition(0)
            } else {
                (mBind.header.isFocused).no {
                    mBind.header.requestFocus()
                }
                mBind.header.scrollToPosition(0)
            }
        }

    }

    override fun initdata() {
        product.addHeaderItem()?.let { items.addAll(it) }

        receiver?.let { mViewModel.addAppStatusBroadcast(it) }


        detectNetStaus()

        startRepeatingTask()

        checkLauncherUpdate()

        mBind.header.requestFocus()

        mBind.header.pivotY = maxVerticalOffset.toFloat()


        mBind.header.setup {
            addType<TypeItem>(R.layout.item_home_header)
            onBind {
                val binding = getBinding<ItemHomeHeaderBinding>()
                val dto = getModel<TypeItem>()
                binding.root.setCallback{
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
                    mViewModel.clickHeaderItem(dto){name,packages->
                        (name==null).yes {
                            toastInstall()
                        }.otherwise {
                            toastInstallPKApp(dto.name, packages)
                        }
                    }
                }
                //dasdas
            }
        }.models = arrayListOf()

        mMainHeaderAdapter =
            MainHeaderAdapter(
                appContext,
                LayoutInflater.from(appContext),
                CopyOnWriteArrayList(),
                headerCallback
            )

        //mBind.header.setAdapter(mMainHeaderAdapter)



        mHMainContentAdapter =
            MainContentAdapter(
                appContext,
                LayoutInflater.from(appContext),
                CopyOnWriteArrayList(),
                contentClick
            )


        mVMainContentAdapter =
            MainContentAdapter(
                appContext,
                LayoutInflater.from(appContext),
                CopyOnWriteArrayList(),
                contentClick
            )
        mAppListAdapter = AppListAdapter(
            appContext,
            getLayoutInflater(),
            CopyOnWriteArrayList(),
            R.layout.item_home_localapps,
            newAppListCallback()
        )
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
                            mViewModel.handleActiveCodeData(it, authDto.msg)
                        }
                    }
                }

        }


        fillHeader()

        mBind.notifyRecycler.linear(RecyclerView.HORIZONTAL).setup {
            addType<Notify>(R.layout.holder_notify_2)
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
            mBind.setting.requestFocus()

            val header = fillData(result)
            header.addAll(items)

            addProduct5TypeItem(header)
            delay(1500)

            setHeader(header)
            isConnectFirst = true

            val item = header[0]
            fillMovice(item)
            delay(2000)
            mBind.setting.clearFocus()
            mBind.header.requestFocus()
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
        receiver?.let { mViewModel.removeAppStatusBroadcast(it) }
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

        var infos = AndroidSystem.getUserApps2(appContext)
        val filteredList = infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos

        if(mBind.horizontalContent.adapter!=null){
            val oldList = mBind.horizontalContent.mutable as MutableList<ApplicationInfo>

            if (filteredList.size != oldList.size) {
                mBind.horizontalContent.bindingAdapter.refreshAppList(oldList, filteredList.toMutableList())
            }
        }



    }

    override fun onStart() {
        super.onStart()
        syncTime()
        syncNotify()
        startLoopTime()
    }


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
        mBind.header.addModels(items)
       // mMainHeaderAdapter!!.replace(items)

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

                //dasdasdasd
                mBind.horizontalContent.setup {
                    addType<Data>(R.layout.item_home_content_por)
                    onBind {
                        //dasdas
                        val binding = getBinding<ItemHomeContentPorBinding>()
                        val dto = getModel<Data>()
                        binding.flRoot.apply {
                            setCallback{
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
                }.models = list

                //mBind.horizontalContent.setAdapter(mHMainContentAdapter)
                //mHMainContentAdapter!!.setLayoutId(layoutId)
                mBind.verticalContent.visibility = View.GONE
                mBind.horizontalContent.visibility = View.VISIBLE
                //mHMainContentAdapter!!.replace(list)
            }

            0, 2 -> {
                if (list?.size ?: 0 > columns * 2) {
                    list = list?.subList(0, columns * 2)
                }

                mBind.verticalContent.setup {
                    addType<Data>{
                        when(direction){
                            0->R.layout.item_home_content_lan
                            else->R.layout.item_home_content_game
                        }
                    }
                    onBind {
                        val flRoot = findView<MyFrameLayoutHouse>(R.id.fl_root)

                        //val binding = getBinding<ItemHomeContentPorBinding>()
                        val dto = getModel<Data>()
                        flRoot.apply {
                            setCallback{
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
                }.models = list

                //mBind.verticalContent.setAdapter(mVMainContentAdapter)
                //mVMainContentAdapter!!.setLayoutId(layoutId)
                mBind.verticalContent.visibility = View.VISIBLE
                mBind.horizontalContent.visibility = View.GONE
                mBind.verticalContent.setNumColumns(columns)
                appContext.let {
                    mBind.verticalContent.setVerticalSpacing(
                        it.resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_1).toInt()
                    )
                }

               // mVMainContentAdapter!!.replace(list)
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


            /*if (AndroidSystem.isEthernetConnected(appContext)) {
                mBind.wifi!!.setImageResource(R.drawable.baseline_lan_100)
            } else {
                mBind.wifi!!.setImageResource(if (isNetworkAvailable) R.drawable.baseline_wifi_100 else R.drawable.baseline_wifi_off_100)
            }*/
            //if (Config.COMPANY == 3) {

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
                mBind.notifyRecycler.bindingAdapter.refresh((mBind.notifyRecycler.mutable)as MutableList<Notify>,notifies)
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
        mBind.horizontalContent.setup {
            addType<ApplicationInfo>(R.layout.item_home_localapps)
            onBind {
                val dto = getModel<ApplicationInfo>()
                val binding = getBinding<ItemHomeLocalappsBinding>()
                binding.root.setCallback{
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
                                            dto.packageName)
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
        }.models = list
       // mAppListAdapter!!.replace(list)
        //mBind.horizontalContent.setAdapter(mAppListAdapter)
        mBind.verticalContent.visibility = View.GONE
        mBind.horizontalContent.visibility = View.VISIBLE
    }

    private fun setProjectorContent() {
        mBind.verticalContent.setNumColumns(product.projectorColumns())
        mBind.verticalContent.isFocusSearchDisabled = false

        mBind.verticalContent.updatePadding(top = com.shudong.lib_dimen.R.dimen.qb_px_10.dimenValue())
        mBind.verticalContent.setup {
            addType<SettingItem>(R.layout.holder_setting_3)
            onBind {
                val mIV = findView<ImageView>(R.id.image)
                val mTitleView = findView<TextView>(R.id.title)
                val rlRoot = findView<RelativeLayoutHouse>(R.id.rl_root)

                val bean = _data as SettingItem
                mIV.setImageResource(bean.ico)
                mTitleView.text = bean.name

                rlRoot.setOnFocusChangeListener { view, b ->
                    view.animScale(b, 1.15f)
                    b.yes {
                        mTitleView.isSelected = true
                    }.otherwise {
                        mTitleView.isSelected = false
                    }
                    if (b) {
                        setRVHeight(false)
                        setExpanded(false)
                    }
                }

                rlRoot.clickNoRepeat {
                    mViewModel.clickProjectorItem(bean) {
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
        }.models = arrayListOf()

        mBind.verticalContent.visibility = View.VISIBLE
        mBind.horizontalContent.visibility = View.GONE
        mBind.verticalContent.addModels(product.addProjectorItem())
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

    private fun selectWork(bean: TypeItem) {
        when (bean.type) {
            Types.TYPE_MY_APPS -> {
                fillApps(false, true)
            }

            Types.TYPE_APP_STORE -> {
                fillAppStore()
            }

            Types.TYPE_PROJECTOR -> {
                setProjectorContent()
            }

            else -> switchMovice(bean)
        }
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

    private fun fillApps(replace: Boolean, isAttach: Boolean) {
        if (replace) {
            useApps.clear()
            var infos = AndroidSystem.getUserApps2(appContext)
            val filteredList = infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos

            useApps.addAll(filteredList)
        }
        if (isAttach) setAppContent(useApps)
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
                if (emptys.isEmpty()) {
                    for (i in 0..9) emptys.add(AppItem())
                }
                setStoreContent(emptys)
                if (!isFullAll && false) {
                    HttpRequest.getAppList(object : AppServiceRequest.Callback<AppListResponse> {
                        override fun onCallback(
                            call: Call<*>,
                            status: Int,
                            result: AppListResponse?
                        ) {
                            isFullAll = false
                            if (!isAdded || call.isCanceled || result == null || result.result == null || result.result.appList == null || result.result.appList.isEmpty()) return
                            if (mBind.header.selectedPosition == -1 || mBind.header.selectedPosition > targetMenus.size - 1 || targetMenus[mBind.header.selectedPosition].type != Types.TYPE_APP_STORE) return
                            App.APP_STORE_ITEMS.addAll(result.result.appList)
                            setStoreContent(App.APP_STORE_ITEMS)
                        }
                    }, Config.USER_ID, null, "hot", null, 1, 20)
                }
            }
        } else {
            setStoreContent(App.APP_STORE_ITEMS)
        }
        isFullAll = true
    }

    private fun setStoreContent(list: List<AppItem>) {

        mBind.horizontalContent.setup {
            addType<AppItem>(R.layout.holder_app_store)
            onBind {
                val binding = getBinding<HolderAppStoreBinding>()
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
        }.models = list

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

                    setDefault()
                }

            } else {
                setDefault()
            }
        } catch (e: Exception) {
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
                    bean?.name?:"",
                    bean?.icon?:"",
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


    val headerCallback = HeaderCallback(
        onClick = { bean ->
            when (bean.type) {
                Types.TYPE_MOVICE -> {
                    val packages = bean.data
                    //"当前名字是====${packages.size}===${packages[0].packageName}"
                    val packagesBean = packages?.get(0)
                    when {
                        packagesBean?.packageName?.contains("com.amazon.amazonvideo.livingroom") == true -> {

                            if (Config.COMPANY == 5) {
                                AndroidSystem.openActivityName(
                                    requireContext(),
                                    "com.amazon.avod.thirdpartyclient",
                                    "com.amazon.avod.thirdpartyclient.LauncherActivity"
                                )

                            } else {
                                val success =
                                    AndroidSystem.jumpPlayer(requireContext(), packages, null)
                                if (!success) {
                                    toastInstallPKApp(bean.name, packages)
                                } else {

                                }
                            }
                        }

                        packagesBean?.packageName?.contains("youtube") == true -> {

                            if (Config.COMPANY == 5) {
                                AndroidSystem.openPackageName(
                                    requireContext(),
                                    "com.google.android.apps.youtube.creator"
                                )
                            } else {
                                val success =
                                    AndroidSystem.jumpPlayer(requireContext(), packages, null)
                                if (!success) {
                                    toastInstallPKApp(bean.name, packages)
                                } else {

                                }
                            }
                        }

                        else -> {
                            try {
                                val success =
                                    packages?.let {
                                        AndroidSystem.jumpPlayer(requireContext(),
                                            it, null)
                                    }
                                if (!success!!) {
                                    toastInstallPKApp(bean.name, packages)

                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                //ToastUtils.show("")
                            }
                        }
                    }


                }

                Types.TYPE_APP_STORE -> {
                    val success = AndroidSystem.jumpAppStore(requireContext())
                    if (!success) toastInstall()
                }

                Types.TYPE_MY_APPS -> {
                    val intent = Intent(requireContext(), AppsActivity::class.java)
                    intent.putExtra(Atts.TYPE, bean.type)
                    startActivity(intent)
                }

                Types.TYPE_GOOGLE_PLAY -> {
                    AndroidSystem.openPackageName(requireContext(), "com.android.vending")
                }

                Types.TYPE_MEDIA_CENTER -> {
                    AndroidSystem.openPackageName(requireContext(), "com.explorer")

                }
            }
        },
        onSelect = { selected, bean ->
            if (selected) {
                setRVHeight(true)
                setExpanded(true)
                try {
                    call?.cancel()
                    uuid = UUID.randomUUID().toString()
                    work(uuid!!, bean)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    )

    //dasdasdas
    val contentClick = ContentCallback(
        onClick = { bean ->
            /* Handle click */
            mViewModel.handleContentClick(bean) {
                if (!it) {
                    toastInstallPKApp(bean.appName ?: "", bean.packageNames)
                }
            }

        },
        onFocus = { hasFocus, bean ->
            /* Handle focus */
            if (hasFocus) {
                setRVHeight(false)
                setExpanded(false)
            }
        }
    )

    fun setRVHeight(isExpanded: Boolean) {
        isExpanded.yes {
            //展开
            mBind.header.width(ViewGroup.LayoutParams.MATCH_PARENT)
        }.otherwise {
            //收缩
            mBind.header.width(5000)
            if ((mBind.header.adapter?.itemCount ?: 0) > 8) {
                mBind.toolbar.height(com.shudong.lib_dimen.R.dimen.qb_px_110.dimenValue())
            } else {
                mBind.toolbar.height(com.shudong.lib_dimen.R.dimen.qb_px_115.dimenValue())
            }
        }
    }


    private fun toastInstallPKApp(name: String, packages: List<PackageName?>?) {
        toastInstallApp(name, object : ToastDialog.Callback {
            override fun onClick(type: Int) {
                if (type == 1) {
                    val pns = arrayOfNulls<String>(packages?.size ?: 0)
                    for (i in pns.indices) {
                        pns[i] = packages?.get(i)?.packageName
                    }
                    AndroidSystem.jumpAppStore(requireContext(), null, pns)
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

    //dasdas
    private fun newAppListCallback(): AppListAdapter.Callback {
        return object : AppListAdapter.Callback {
            override fun onSelect(selected: Boolean) {
                if (selected) {
                    setRVHeight(false)
                    setExpanded(false)
                }
            }

            override fun onClick(bean: ApplicationInfo) {
                try {
                    openApp(bean)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onMenuClick(bean: ApplicationInfo) {
                appMenu(bean)
            }
        }
    }

    private fun openApp(bean: ApplicationInfo) {
        AndroidSystem.openPackageName(requireContext(), bean.packageName)
    }

    private fun appMenu(bean: ApplicationInfo) {
        val dialog = AppDialog.newInstance(bean)
        dialog.setCallback(object : AppDialog.Callback {
            override fun onOpen() {
                AndroidSystem.openPackageName(requireContext(), bean.packageName)
            }

        })
        dialog.show(getChildFragmentManager(), AppDialog.TAG)
    }

    private fun setExpanded(isExpanded: Boolean) {
        mBind.appBar.setExpanded(isExpanded)
    }


    inner class InnerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_REPLACED -> {

                    var infos = AndroidSystem.getUserApps2(appContext)
                    val filteredList =
                        infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos

                    val oldList = mBind.horizontalContent.mutable as MutableList<ApplicationInfo>

                    if (filteredList.size != oldList.size) {
                       // mAppListAdapter?.refresh(filteredList.toMutableList())
                        mBind.horizontalContent.bindingAdapter.refreshAppList(mBind.horizontalContent.mutable as MutableList<ApplicationInfo>, filteredList.toMutableList())
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
