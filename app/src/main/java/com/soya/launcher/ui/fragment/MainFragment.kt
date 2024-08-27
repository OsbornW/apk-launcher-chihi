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
import android.os.storage.StorageManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.NetworkUtils
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.open.system.SystemUtils
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.REFRESH_HOME
import com.shudong.lib_base.ext.UPDATE_HOME_LIST
import com.shudong.lib_base.ext.animScale
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
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
import com.soya.launcher.adapter.NotifyAdapter
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.adapter.StoreAdapter
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
import com.soya.launcher.ext.isH6
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.ext.isSDCard
import com.soya.launcher.ext.isShowUpdate
import com.soya.launcher.ext.isUDisk
import com.soya.launcher.http.AppServiceRequest
import com.soya.launcher.http.HttpRequest
import com.soya.launcher.http.HttpRequest.checkVersion
import com.soya.launcher.http.HttpRequest.uidPull
import com.soya.launcher.http.ServiceRequest
import com.soya.launcher.http.response.AppListResponse
import com.soya.launcher.http.response.VersionResponse
import com.soya.launcher.manager.FilePathMangaer
import com.soya.launcher.ui.activity.AboutActivity
import com.soya.launcher.ui.activity.AppsActivity
import com.soya.launcher.ui.activity.ChooseGradientActivity
import com.soya.launcher.ui.activity.GradientActivity
import com.soya.launcher.ui.activity.HomeGuideGroupGradientActivity
import com.soya.launcher.ui.activity.InstallModeActivity
import com.soya.launcher.ui.activity.LoginActivity
import com.soya.launcher.ui.activity.MainActivity
import com.soya.launcher.ui.activity.ScaleScreenActivity
import com.soya.launcher.ui.activity.SearchActivity
import com.soya.launcher.ui.activity.SettingActivity
import com.soya.launcher.ui.activity.WifiListActivity
import com.soya.launcher.ui.dialog.AppDialog
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.AppUtils
import com.soya.launcher.utils.PreferencesUtils
import com.soya.launcher.utils.md5
import com.soya.launcher.utils.showLoadingViewDismiss
import com.soya.launcher.view.ImageViewHouse
import com.soya.launcher.view.NoDragVerticalGridView
import com.soya.launcher.net.viewmodel.HomeViewModel
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.UpdateAppsActivity
import com.soya.launcher.view.RelativeLayoutHouse
import com.thumbsupec.lib_base.toast.ToastUtils
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

class MainFragment : BaseWallPaperFragment<FragmentMainBinding, HomeViewModel>(), AppBarLayout.OnOffsetChangedListener, View.OnClickListener {
    private val useApps: MutableList<ApplicationInfo> = ArrayList()
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val exec = Executors.newCachedThreadPool()

    private var uiHandler: Handler? = null
    private var uuid: String? = null
    private val call: Call<*>? = null
    private var homeCall: Call<*>? = null
    private var isReqHome = false
    private var receiver: InnerReceiver? = null
    private var wallpaperReceiver: WallpaperReceiver? = null
    private var maxVerticalOffset = 0f
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
    private var mStoreAdapter: StoreAdapter? = null
    private var requestTime = System.currentTimeMillis()
    private var isExpanded = false
    lateinit var mNotifyAdapter: NotifyAdapter


    override fun initView() {
        maxVerticalOffset = resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_60)
        uiHandler = Handler()
        receiver = InnerReceiver()
        wallpaperReceiver = WallpaperReceiver()
        val infos = AndroidSystem.getUserApps2(appContext)
        val filteredList = infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos

        useApps.addAll(filteredList)
    }

    override fun initObserver() {
        this.obseverLiveEvent<Boolean>("refreshdefault") {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    (NetworkUtils.isConnected() && NetworkUtils.isAvailable()).no {
                        // 达大厦
                        withContext(Dispatchers.Main) {
                            setDefault()
                        }
                    }
                }
            }
        }

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

    }

    override fun initdata() {
        if(Config.COMPANY!=5){
            items.addAll(
                Arrays.asList(
                    *arrayOf(
                        TypeItem(
                            getString(R.string.app_store),
                            R.drawable.store,
                            0,
                            Types.TYPE_APP_STORE,
                            TypeItem.TYPE_ICON_IMAGE_RES,
                            TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                        ),

                        )
                )
            )

            items.addAll(
                Arrays.asList(
                    *arrayOf(
                        TypeItem(
                            getString(R.string.apps),
                            R.drawable.app_list,
                            0,
                            Types.TYPE_MY_APPS,
                            TypeItem.TYPE_ICON_IMAGE_RES,
                            TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                        )
                    )
                )
            )

        }

        if (Config.COMPANY == 0 || Config.COMPANY == 9) {
            items.add(
                TypeItem(
                    getString(R.string.pojector),
                    R.drawable.projector,
                    0,
                    Types.TYPE_PROJECTOR,
                    TypeItem.TYPE_ICON_IMAGE_RES,
                    TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                )
            )
        }
        if (Config.COMPANY == 4) {
            items.add(
                TypeItem(
                    getString(R.string.tool),
                    R.drawable.tool,
                    0,
                    Types.TYPE_TOOL,
                    TypeItem.TYPE_ICON_IMAGE_RES,
                    TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
                )
            )
        }


        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addDataScheme("package")
        activity!!.registerReceiver(receiver, filter)
        val filter1 = IntentFilter()
        filter1.addAction(IntentAction.ACTION_UPDATE_WALLPAPER)
        filter1.addAction(IntentAction.ACTION_RESET_SELECT_HOME)
        filter1.addAction(Intent.ACTION_SCREEN_ON)
        filter1.addAction(Intent.ACTION_SCREEN_OFF)
        filter1.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter1.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        filter1.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
        filter1.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
        filter1.addAction("android.hardware.usb.action.USB_STATE")
        activity!!.registerReceiver(wallpaperReceiver, filter1)

        detectNetStaus()

        startRepeatingTask()

        mBind.header.requestFocus()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                (NetworkUtils.isConnected() && NetworkUtils.isAvailable()).yes {
                    withContext(Dispatchers.Main) {
                        checkVersion()
                    }
                }
            }
        }


        mBind.horizontalContent.addItemDecoration(
            HSlideMarginDecoration(
                resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_10),
                resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_2)
            )
        )
        mBind.header.addItemDecoration(
            HSlideMarginDecoration(
                resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_10),
                resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_2)
            )
        )
        mBind.header.pivotY = maxVerticalOffset
        mStoreAdapter = StoreAdapter(
            activity,
            getLayoutInflater(),
            CopyOnWriteArrayList(),
            newStoreClickCallback()
        )
        mNotifyAdapter = NotifyAdapter(
            appContext,
            LayoutInflater.from(appContext),
            CopyOnWriteArrayList(),
            if (Config.COMPANY == 3) R.layout.holder_notify else R.layout.holder_notify_2
        )
        mMainHeaderAdapter =
            MainHeaderAdapter(
                appContext,
                LayoutInflater.from(appContext),
                CopyOnWriteArrayList(),
                newHeaderCallback()
            )

        mBind.header.setAdapter(mMainHeaderAdapter)

        mHMainContentAdapter =
            MainContentAdapter(
                appContext,
                LayoutInflater.from(appContext),
                CopyOnWriteArrayList(),
                newContentCallback()
            )


        mVMainContentAdapter =
            MainContentAdapter(
                appContext,
                LayoutInflater.from(appContext),
                CopyOnWriteArrayList(),
                newContentCallback()
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
            val uniqueID = DeviceUtils.getUniqueDeviceId()
                .subSequence(0, DeviceUtils.getUniqueDeviceId().length - 1)
            //激活码
            val activeCode = AppCacheBase.activeCode
            val versionValue = 1003
            // 渠道ID
            val chanelId = BuildConfig.CHANNEL

            val childChanel = "S10001"
            // 时间戳
            val time = System.currentTimeMillis() / 1000
            // 密码盐
            val pwd = "TKPCpTVZUvrI"
            // 待加密的字符串（(d+e+c+b+a+f+密码盐）
            val toBeEncryptedString =
                "$chanelId$childChanel$versionValue$activeCode$uniqueID$time$pwd"
            // 对字符串进行MD5加密
            val md5String = toBeEncryptedString.md5()

            val params = mapOf(
                //唯一ID
                "a" to uniqueID,
                // 激活码
                "b" to activeCode.toLong(),
                // API 版本值
                "c" to versionValue,
                //渠道号
                "d" to chanelId,
                // 子渠道号
                "e" to childChanel,
                // 当前时间戳（秒）
                "f" to time,
                // 签名值  md5(d+e+c+b+a+f+密码盐)
                "s" to md5String,
            )
            val jsonObject = JSONObject(params)

            OkGo.post("https://api.freedestop.com/u/client")
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(object : StringCallback() {
                    override fun onSuccess(s: String?, call: okhttp3.Call?, response: Response?) {
                        lifecycleScope.launch {
                            delay(1000)
                            showLoadingViewDismiss()
                            val authBean = s?.jsonToBean<AuthBean>()
                            (authBean?.status == 200).yes {
                                authBean?.code?.let {
                                    "开始判断msg==="
                                    BuildConfig.DEBUG.no {
                                        it.getResult(authBean.msg)
                                    }

                                }

                            }.otherwise {

                            }
                        }
                    }

                    override fun onError(call: okhttp3.Call?, response: Response?, e: Exception?) {

                    }

                })

        }


        fillHeader()
        mBind.notifyRecycler.setLayoutManager(
            LinearLayoutManager(
                appContext,
                RecyclerView.HORIZONTAL,
                false
            )
        )
        mBind.notifyRecycler.setAdapter(mNotifyAdapter)

    }

    private fun Long.getResult(msg: String?) {
        (msg == null).no {
            ToastUtils.show(msg)
            AppCacheBase.isActive = false
            (activity as MainActivity).switchAuthFragment()
        }.otherwise {
            when (this) {
                10000L -> {
                    AppCacheBase.isActive = true
                    //showLoadingViewDismiss()
                    /* ToastUtils.show("Success")
                     lifecycleScope.launch {
                         delay(500)
                         //repeatOnLifecycle(Lifecycle.State.RESUMED){
                         sendLiveEventData(ACTIVE_SUCCESS, true)
                         // }

                     }*/

                }

                10004L -> {
                    ToastUtils.show("Invalid PIN, please try again! ")
                    AppCacheBase.isActive = false
                    (activity as MainActivity).switchAuthFragment()
                }

                else -> {
                    ToastUtils.show("Failed, please try again!")
                    AppCacheBase.isActive = false
                    (activity as MainActivity).switchAuthFragment()
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
            /* if (result.getData().reg_id != null) PreferencesUtils.setProperty(
                 Atts.RECENTLY_MODIFIED,
                 result.getData().reg_id
             )*/
            val item = header[0]
            fillMovice(item)


            delay(2000)
            mBind.setting.clearFocus()
            //delay(500)
            mBind.header.requestFocus()
            //requestFocus(mBind.header, 0)
        }
        /*if (BuildConfig.FLAVOR == "hongxin_H27002") {
            requestFocus(mBind.header, 500)
        }*/
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
        activity!!.unregisterReceiver(receiver)
        activity!!.unregisterReceiver(wallpaperReceiver)
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

        if (filteredList.size != mAppListAdapter?.getDataList()?.size) {
            mAppListAdapter?.refresh(filteredList.toMutableList())
            useApps.clear()
            useApps.addAll(filteredList)

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
                    if(b)rlSetting.isVisible = true else rlSetting.visibility = View.INVISIBLE
                }
            }

            mBind.gradient.let {
                it.setOnFocusChangeListener { view, b ->
                    if(b)projection.isVisible = true else projection.visibility = View.INVISIBLE

                }
            }

            mBind.hdmi.let {
                it.setOnFocusChangeListener { view, b ->
                    if(b)hdml.isVisible = true else hdml.visibility = View.INVISIBLE

                }
            }

            mBind.wifi.let {
                it.setOnFocusChangeListener { view, b ->
                    if(b)rlWifi.isVisible = true else rlWifi.visibility = View.INVISIBLE

                }
            }


        }
    }

    

    private fun setHeader(items: List<TypeItem>) {
        targetMenus.clear()
        targetMenus.addAll(items)
        mMainHeaderAdapter!!.replace(items)

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
                mBind.horizontalContent.setAdapter(mHMainContentAdapter)
                mHMainContentAdapter!!.setLayoutId(layoutId)
                mBind.verticalContent.visibility = View.GONE
                mBind.horizontalContent.visibility = View.VISIBLE
                mHMainContentAdapter!!.replace(list)
            }

            0, 2 -> {
                if (list?.size ?: 0 > columns * 2) {
                    list = list?.subList(0, columns * 2)
                }
                mBind.verticalContent.setAdapter(mVMainContentAdapter)
                mVMainContentAdapter!!.setLayoutId(layoutId)
                mBind.verticalContent.visibility = View.VISIBLE
                mBind.horizontalContent.visibility = View.GONE
                mBind.verticalContent.setNumColumns(columns)
                appContext.let {
                    mBind.verticalContent.setVerticalSpacing(
                        it.resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_1).toInt()
                    )
                }

                mVMainContentAdapter!!.replace(list)
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
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) notifies.add(Notify(R.drawable.baseline_bluetooth_100,3))
                val deviceHashMap =
                    (appContext.getSystemService(Context.USB_SERVICE) as UsbManager).deviceList

                val isInsertUDisk = requireActivity().isUDisk()

                isInsertUDisk.yes {
                    notifies.add(Notify(R.drawable.baseline_usb_100,0))
                }

                /*for (i in 0 until deviceHashMap.size) {
                        notifies.add(Notify(R.drawable.baseline_usb_100))
                 }*/
                if (SystemUtils.isApEnable(appContext)) notifies.add(Notify(R.drawable.baseline_wifi_tethering_100_2,2))
                val storageManager = appContext.getSystemService(
                    StorageManager::class.java
                )

                val isInsertSDCard = requireActivity().isSDCard()

                isInsertSDCard.yes {
                    notifies.add(Notify(R.drawable.baseline_sd_storage_100,1))
                }
                /*for (volume in storageManager.storageVolumes) {
                        if (!volume.isEmulated) notifies.add(Notify(R.drawable.baseline_sd_storage_100))
                }*/
            if(notifies.size != mNotifyAdapter.getDataList().size){
                mNotifyAdapter.refresh(notifies)
            }



            //}
        })
    }


    private fun syncTime() {
        val is24 = AppUtils.is24Display(appContext)
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
        mAppListAdapter!!.replace(list)
        mBind.horizontalContent.setAdapter(mAppListAdapter)
        mBind.verticalContent.visibility = View.GONE
        mBind.horizontalContent.visibility = View.VISIBLE
    }

    private fun setProjectorContent() {
        //dasdas
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                appContext,
                LayoutInflater.from(appContext),
                newProjectorCallback(),
                R.layout.holder_setting_3
            )
        )
        val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_MEDIUM,
            false
        )
        mBind.verticalContent.setNumColumns(4)
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
                    if (b) setExpanded(false)
                }

                rlRoot.clickNoRepeat {
                    val bean = _data as SettingItem
                    when (bean.type) {
                        Projector.TYPE_SETTING -> {
                            isRK3326().yes {
                                AndroidSystem.openActivityName(
                                    appContext,
                                    "com.lei.hxkeystone",
                                    "com.lei.hxkeystone.ScaleActivity"
                                )
                            }.otherwise {
                                startActivity(Intent(appContext, ScaleScreenActivity::class.java))
                            }
                        }

                        Projector.TYPE_PROJECTOR_MODE -> {
                            when {
                                isH6() -> {
                                    startKtxActivity<InstallModeActivity>()
                                }

                                else -> {
                                    val success = AndroidSystem.openProjectorMode(appContext)
                                    if (!success) toastInstall()
                                }
                            }

                        }

                        Projector.TYPE_HDMI -> {
                            val success = AndroidSystem.openProjectorHDMI(appContext)
                            if (!success) toastInstall()
                        }

                        Projector.TYPE_SCREEN -> {
                            when {
                                isH6() -> {
                                    startKtxActivity<GradientActivity>()
                                }

                                else -> {
                                    startActivity(
                                        Intent(
                                            appContext,
                                            ChooseGradientActivity::class.java
                                        )
                                    )
                                }
                            }


                        }
                    }
                }

            }
        }.models = arrayListOf()


        // mBind.horizontalContent!!.setAdapter(itemBridgeAdapter)
        mBind.verticalContent.visibility = View.VISIBLE
        mBind.horizontalContent.visibility = View.GONE
        val list: MutableList<SettingItem?> = ArrayList()
        list.add(
            SettingItem(
                Projector.TYPE_PROJECTOR_MODE,
                appContext.getString(R.string.project_mode),
                R.drawable.baseline_model_training_100
            )
        )
        list.add(
            SettingItem(
                Projector.TYPE_SETTING,
                appContext.getString(R.string.project_crop),
                R.drawable.baseline_crop_100
            )
        )
        list.add(
            SettingItem(
                Projector.TYPE_SCREEN,
                appContext.getString(R.string.project_gradient),
                R.drawable.baseline_screenshot_monitor_100
            )
        )
        list.add(
            SettingItem(
                Projector.TYPE_HDMI,
                appContext.getString(R.string.project_hdmi),
                R.drawable.baseline_settings_input_hdmi_100
            )
        )
        //arrayObjectAdapter.addAll(0, list)
        mBind.verticalContent.addModels(list)
    }

    private fun setToolContent() {
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                appContext,
                getLayoutInflater(),
                newToolCallback(),
                R.layout.holder_setting_3
            )
        )
        val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_MEDIUM,
            false
        )
        mBind.horizontalContent.setAdapter(itemBridgeAdapter)
        mBind.verticalContent.visibility = View.GONE
        mBind.horizontalContent.visibility = View.VISIBLE
        val list: MutableList<SettingItem?> = ArrayList()
        list.add(
            SettingItem(
                Tools.TYPE_HDMI,
                getString(R.string.project_hdmi),
                R.drawable.baseline_settings_input_hdmi_100
            )
        )
        list.add(
            SettingItem(
                Tools.TYPE_FILE,
                getString(R.string.file_management),
                R.drawable.baseline_sd_storage_100_3
            )
        )
        arrayObjectAdapter.addAll(0, list)
    }

    private fun newProjectorCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem) {
                if (selected) setExpanded(false)
            }

            override fun onClick(bean: SettingItem) {
                when (bean.type) {
                    Projector.TYPE_SETTING -> {
                        isRK3326().yes {
                            AndroidSystem.openActivityName(
                                appContext,
                                "com.lei.hxkeystone",
                                "com.lei.hxkeystone.ScaleActivity"
                            )
                        }.otherwise {
                            startActivity(Intent(activity, ScaleScreenActivity::class.java))
                        }
                    }

                    Projector.TYPE_PROJECTOR_MODE -> {
                        val success = AndroidSystem.openProjectorMode(appContext)
                        if (!success) toastInstall()
                    }

                    Projector.TYPE_HDMI -> {
                        val success = AndroidSystem.openProjectorHDMI(appContext)
                        if (!success) toastInstall()
                    }

                    Projector.TYPE_SCREEN -> {
                        startActivity(Intent(appContext, ChooseGradientActivity::class.java))
                    }
                }
            }
        }
    }

    private fun newToolCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem) {
                if (selected) setExpanded(false)
            }

            override fun onClick(bean: SettingItem) {
                when (bean.type) {
                    Tools.TYPE_HDMI -> AndroidSystem.openPackageName(
                        appContext,
                        "com.mediatek.wwtv.tvcenter"
                    )

                    Tools.TYPE_FILE -> AndroidSystem.openPackageName(
                        appContext,
                        "com.conocx.fileexplorer"
                    )
                }
            }
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        isExpanded = verticalOffset != 0
        val value = (1f - abs((verticalOffset / 2f).toDouble()) / maxVerticalOffset).toFloat()
        mBind.header.scaleX = value
        mBind.header.scaleY = value
    }

    //var adController:Controller?=null
    override fun onClick(v: View) {
        if (v == mBind.setting) {
            if (Config.COMPANY == 4) {
                AndroidSystem.openSystemSetting(appContext)
            } else {
                startActivity(Intent(appContext, SettingActivity::class.java))
            }
            //loadJar()
            //requireActivity().initializeAd(rlAD!!,this)
            //requireActivity().startAd()

            /* Ad.get().setEnableLog(true)
             if(adController==null){
                 adController = Ad.get().begin(appContext)
                     .container(rlAD)
                     .lifecycleOwner(this)
                     .start();
             }else{
                 adController?.start(rlAD)
             }*/

            //AndroidSystem.openSystemSetting(getActivity());
        } else if (v == mBind.search) {
            startActivity(Intent(activity, SearchActivity::class.java))
        } else if (v == mBind.wifi) {
            if (Config.COMPANY == 3 || Config.COMPANY == 4) {
                AndroidSystem.openWifiSetting(appContext)
            } else {
                startActivity(Intent(activity, WifiListActivity::class.java))
            }
        } else if (v == mBind.login) {
            startActivity(Intent(activity, LoginActivity::class.java))
        } else if (v == mBind.help) {
            startActivity(Intent(activity, AboutActivity::class.java))
        } else if (v == mBind.hdmi) {
            AndroidSystem.openProjectorHDMI(appContext)
        } else if (v == mBind.gradient) {
            //startActivity(Intent(appContext, HomeGuideGroupGradientActivity::class.java))

            // startActivity(Intent(appContext, ChooseGradientActivity::class.java))
            when {
                isH6() -> {
                    startKtxActivity<GradientActivity>()
                }

                else -> {
                    startKtxActivity<HomeGuideGroupGradientActivity>()
                }
            }


            /*
            * 判断是否打开了自动校准，否则先打开自动校准
            * 然后跳转SDK自动校准页面
            * */
            // 是否处于自动校准
            /*  var isAutoCalibration =
                  ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1
              isAutoCalibration.no {
                  // 没有打开自动校准
                  //开启自动校准
                  ASystemProperties.set("persist.vendor.gsensor.enable", "1")
              }*/

            //跳转自动校准页面
            /* AndroidSystem.openActivityName(
                 appContext,
                 "com.hxdevicetest",
                 "com.hxdevicetest.CheckGsensorActivity"
             )*/

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

            Types.TYPE_TOOL -> {
                setToolContent()
            }

            else -> switchMovice(bean)
        }
    }


    private fun newHeaderCallback(): MainHeaderAdapter.Callback {
        return object : MainHeaderAdapter.Callback {
            override fun onClick(bean: TypeItem) {
                when (bean.type) {
                    Types.TYPE_MOVICE -> {
                        val packages = bean.data
                        "当前名字是====${packages.size}===${packages[0].packageName}"
                        when {
                            packages[0].packageName?.contains("com.amazon.amazonvideo.livingroom") == true -> {

                                if (Config.COMPANY == 5) {
                                    AndroidSystem.openActivityName(
                                        appContext,
                                        "com.amazon.avod.thirdpartyclient",
                                        "com.amazon.avod.thirdpartyclient.LauncherActivity"
                                    )

                                } else {
                                    val success = AndroidSystem.jumpPlayer(appContext, packages, null)
                                    if (!success) {
                                        toastInstallPKApp(bean.name, packages)
                                    } else {

                                    }
                                }
                            }

                            packages[0].packageName?.contains("youtube") == true -> {

                                if (Config.COMPANY == 5) {
                                    AndroidSystem.openPackageName(
                                        appContext,
                                        "com.google.android.apps.youtube.creator"
                                    )
                                } else {
                                    val success = AndroidSystem.jumpPlayer(appContext, packages, null)
                                    if (!success) {
                                        toastInstallPKApp(bean.name, packages)
                                    } else {

                                    }
                                }
                            }

                            else -> {
                                try {
                                    val success = AndroidSystem.jumpPlayer(appContext, packages, null)
                                    if (!success) {
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
                        val success = AndroidSystem.jumpAppStore(appContext)
                        if (!success) toastInstall()
                    }

                    Types.TYPE_MY_APPS -> {
                        "开始启动1"
                        val intent = Intent(appContext, AppsActivity::class.java)
                        intent.putExtra(Atts.TYPE, bean.type)
                        startActivity(intent)
                    }

                    Types.TYPE_GOOGLE_PLAY -> {
                        AndroidSystem.openPackageName(appContext, "com.android.vending")
                    }

                    Types.TYPE_MEDIA_CENTER -> {
                        AndroidSystem.openPackageName(appContext, "com.explorer")

                    }
                }
            }

            override fun onSelect(selected: Boolean, bean: TypeItem) {

                if (selected) {
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
        }
    }

    private fun toastInstall() {
        toast(getString(R.string.place_install_app), ToastDialog.MODE_DEFAULT, null)
    }

    private fun toastInstallApp(name: String, callback: ToastDialog.Callback) {
        "当前的APP名字是=====$name"
        if (name == null || name == "") {
            ToastUtils.show("当前APP名字是空的")
        } else {
            toast(getString(R.string.place_install, name), ToastDialog.MODE_DEFAULT, callback)

        }
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
        //requestHome()
    }

    private fun requestHome() {
        if (isReqHome || isConnectFirst) return
        isReqHome = true
        sendLiveEventData(REFRESH_HOME, true)
        // homeCall = HttpRequest.getHomeContents(newMoviceListCallback())
    }

    private fun fillApps(replace: Boolean, isAttach: Boolean) {
        if (replace) {
            useApps.clear()
            var infos = AndroidSystem.getUserApps2(appContext)
            val filteredList = infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos

            /*if (infos.size > 8) {
                infos = infos.subList(0, 8)
            }*/
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
        mStoreAdapter!!.replace(list)
        mBind.horizontalContent.setAdapter(mStoreAdapter)
        mBind.verticalContent.visibility = View.GONE
        mBind.horizontalContent.visibility = View.VISIBLE
    }

    /* private fun local(filePath: String, direction: Int, columns: Int, layoutId: Int) {
         val files = AndroidSystem.getAssetsFileNames(activity, filePath)
         val list: MutableList<Data> = ArrayList(files.size)
         for (item in files) {
             list.add(Data(Types.TYPE_UNKNOW, null, "$filePath/$item", Movice.PIC_ASSETS))
         }
         setMoviceContent(list, direction, columns, layoutId)
     }*/

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
                        },500)
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
            header.addAll(1,menuList)
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
                    bean?.name,
                    bean?.icon,
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
                        "Max", "Disney+", "Hulu", "Google play", "media center" -> {
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

    private fun newStoreClickCallback(): StoreAdapter.Callback {
        return object : StoreAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: AppItem) {
                if (selected) setExpanded(false)
            }

            override fun onClick(bean: AppItem) {
                if (!TextUtils.isEmpty(bean.appDownLink)) AndroidSystem.jumpAppStore(
                    appContext,
                    Gson().toJson(bean),
                    null
                )
            }
        }
    }

    private fun newContentCallback(): MainContentAdapter.Callback {
        return object : MainContentAdapter.Callback {
            override fun onClick(bean: Data) {
                var skip = false
                if (bean.packageNames != null) {
                    bean.packageNames?.let {
                        for (appPackage in it) {
                            if (App.SKIP_PAKS.contains(appPackage.packageName)) {
                                skip = true
                                break
                            }
                        }
                    }

                }


                var success = false
                success = if (skip) {
                    AndroidSystem.jumpVideoApp(appContext, bean.packageNames, null)
                } else {
                    AndroidSystem.jumpVideoApp(appContext, bean.packageNames, bean.url)
                }
                if (!success) {
                    toastInstallPKApp(bean.appName ?: "", bean.packageNames)
                }
            }

            override fun onFouces(hasFocus: Boolean, bean: Data) {
                if (hasFocus) {
                    setExpanded(false)
                }
            }
        }
    }

    private fun toastInstallPKApp(name: String, packages: List<PackageName?>?) {
        toastInstallApp(name) { type ->
            if (type == 1) {
                val pns = arrayOfNulls<String>(packages?.size ?: 0)
                for (i in pns.indices) {
                    pns[i] = packages?.get(i)?.packageName
                }
                AndroidSystem.jumpAppStore(appContext, null, pns)
            }
        }
    }

    private fun deleteAllPic() {
        appContext.filesDir.absolutePath.deleteAllImages()
    }

    private fun switchMovice(bean: TypeItem) {
        fillMovice(bean)
    }

    private fun newAppListCallback(): AppListAdapter.Callback {
        return object : AppListAdapter.Callback {
            override fun onSelect(selected: Boolean) {
                if (selected) setExpanded(false)
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
        AndroidSystem.openPackageName(appContext, bean.packageName)
    }

    private fun appMenu(bean: ApplicationInfo) {
        val dialog = AppDialog.newInstance(bean)
        dialog.setCallback { AndroidSystem.openPackageName(appContext, bean.packageName) }
        dialog.show(getChildFragmentManager(), AppDialog.TAG)
    }

    private fun setExpanded(isExpanded: Boolean) {
        mBind.appBar.setExpanded(isExpanded)
    }


    private fun checkVersion() {
        checkVersion(object : ServiceRequest.Callback<VersionResponse> {
            override fun onCallback(call: Call<*>, status: Int, result: VersionResponse?) {
                if (!isAdded || call.isCanceled || result == null || result.data == null) return
                val version = result.data
                if (version.version > BuildConfig.VERSION_CODE && Config.CHANNEL == version.channel) {
                    PreferencesUtils.setProperty(Atts.UPGRADE_VERSION, version.version.toInt())
                    AndroidSystem.jumpUpgrade(appContext, version)
                }
            }
        })
    }

    inner class InnerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                IntentAction.ACTION_UPDATE_WALLPAPER -> updateWallpaper()
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_REPLACED -> {

                    var infos = AndroidSystem.getUserApps2(appContext)
                    val filteredList =
                        infos.toMutableList().let { product.filterRepeatApps(it) } ?: infos
                    if (filteredList.size != mAppListAdapter?.getDataList()?.size) {
                        mAppListAdapter?.refresh(filteredList.toMutableList())
                        useApps.clear()
                        useApps.addAll(mAppListAdapter?.getDataList()!!)

                    }


                }

            }
        }
    }

    inner class WallpaperReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                IntentAction.ACTION_UPDATE_WALLPAPER -> updateWallpaper()
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {}
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {}
                IntentAction.ACTION_RESET_SELECT_HOME -> {
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
