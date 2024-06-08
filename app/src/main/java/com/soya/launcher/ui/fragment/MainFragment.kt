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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.open.system.SystemUtils
import com.shudong.lib_base.ext.d
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.App
import com.soya.launcher.BuildConfig
import com.soya.launcher.R
import com.soya.launcher.adapter.AppListAdapter
import com.soya.launcher.adapter.MainContentAdapter
import com.soya.launcher.adapter.MainHeaderAdapter
import com.soya.launcher.adapter.NotifyAdapter
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.adapter.StoreAdapter
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.AppPackage
import com.soya.launcher.bean.Movice
import com.soya.launcher.bean.MyRunnable
import com.soya.launcher.bean.Notify
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.bean.WeatherData
import com.soya.launcher.config.Config
import com.soya.launcher.decoration.HSlideMarginDecoration
import com.soya.launcher.enums.Atts
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.enums.Tools
import com.soya.launcher.enums.Types
import com.soya.launcher.http.AppServiceRequest
import com.soya.launcher.http.HttpRequest
import com.soya.launcher.http.HttpRequest.checkVersion
import com.soya.launcher.http.ServiceRequest
import com.soya.launcher.http.response.AppListResponse
import com.soya.launcher.http.response.HomeResponse
import com.soya.launcher.http.response.VersionResponse
import com.soya.launcher.manager.FilePathMangaer
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.ui.activity.AboutActivity
import com.soya.launcher.ui.activity.AppsActivity
import com.soya.launcher.ui.activity.ChooseGradientActivity
import com.soya.launcher.ui.activity.HomeGuideGroupGradientActivity
import com.soya.launcher.ui.activity.LoginActivity
import com.soya.launcher.ui.activity.ScaleScreenActivity
import com.soya.launcher.ui.activity.SearchActivity
import com.soya.launcher.ui.activity.SettingActivity
import com.soya.launcher.ui.activity.WeatherActivity
import com.soya.launcher.ui.activity.WifiListActivity
import com.soya.launcher.ui.dialog.AppDialog
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.AppUtils
import com.soya.launcher.utils.FileUtils
import com.soya.launcher.utils.PreferencesUtils
import com.thumbsupec.lib_base.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import java.io.File
import java.io.FileReader
import java.nio.charset.StandardCharsets
import java.util.Arrays
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MainFragment : AbsFragment(), AppBarLayout.OnOffsetChangedListener, View.OnClickListener {
    private val IS_TEST = false
    private val MAX_WEATHER_TIME = 90 * 1000
    private val useApps: MutableList<ApplicationInfo> = ArrayList()
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val exec = Executors.newCachedThreadPool()
    private var mHeaderGrid: HorizontalGridView? = null
    private var mHorizontalContentGrid: HorizontalGridView? = null
    private var mVerticalContentGrid: VerticalGridView? = null
    private var mAppBarLayout: AppBarLayout? = null
    private var mRootView: View? = null
    private var mSettingView: View? = null
    private var mWeatherView: ImageView? = null
    private var mSearchView: View? = null
    private var mWifiView: ImageView? = null
    private var mLoginView: View? = null
    private var mTimeView: TextView? = null
    private var mSegmentView: TextView? = null
    private var mHelpView: View? = null
    private var mTestView: TextView? = null
    private var mNotifyRecycler: RecyclerView? = null
    private var mGradientView: View? = null
    private var mHdmiView: View? = null
    private var mNotifyAdapter: NotifyAdapter? = null
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
    private var lastWeatherTime: Long = -1
    private val lastCheckPushTime = System.currentTimeMillis()
    private val isFullStoreNow = false
    private var mMainHeaderAdapter: MainHeaderAdapter? = null
    private var mHMainContentAdapter: MainContentAdapter? = null
    private var mVMainContentAdapter: MainContentAdapter? = null
    private var mAppListAdapter: AppListAdapter? = null
    private var mStoreAdapter: StoreAdapter? = null
    private var requestTime = System.currentTimeMillis()
    private var isExpanded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        maxVerticalOffset = resources.getDimension(R.dimen.until_collapsed_height)
        uiHandler = Handler()
        receiver = InnerReceiver()
        wallpaperReceiver = WallpaperReceiver()
        useApps.addAll(AndroidSystem.getUserApps2(requireContext()))
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

        if (Config.COMPANY==5){

        }else{
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
        if (Config.COMPANY == 0) {
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
        syncWeather()
        syncTime()
        syncNotify()
        startLoopTime()
    }

    override fun onStart() {
        super.onStart()
        syncWeather()
        syncTime()
        syncNotify()
        startLoopTime()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mHeaderGrid, 350)


        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                (NetworkUtils.isConnected() && NetworkUtils.isAvailable()).yes {
                    withContext(Dispatchers.Main) {
                        checkVersion()
                    }
                }
            }
        }


        //uidPull()
    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mHeaderGrid = view.findViewById(R.id.header)
        mHorizontalContentGrid = view.findViewById(R.id.horizontal_content)
        mVerticalContentGrid = view.findViewById(R.id.vertical_content)
        mAppBarLayout = view.findViewById(R.id.app_bar)
        mRootView = view.findViewById(R.id.root)
        mSettingView = view.findViewById(R.id.setting)
        mWeatherView = view.findViewById(R.id.weather)
        mSearchView = view.findViewById(R.id.search)
        mWifiView = view.findViewById(R.id.wifi)
        mLoginView = view.findViewById(R.id.login)
        mSegmentView = view.findViewById(R.id.loop_segment)
        mTimeView = view.findViewById(R.id.loop_time)
        mHelpView = view.findViewById(R.id.help)
        mTestView = view.findViewById(R.id.test)
        mNotifyRecycler = view.findViewById(R.id.notify_recycler)
        mHdmiView = view.findViewById(R.id.hdmi)
        val rlSetting = view.findViewById<RelativeLayout>(R.id.rl_setting)
        val rlWifi = view.findViewById<RelativeLayout>(R.id.rl_wifi)
        mGradientView = view.findViewById(R.id.gradient)

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){
            mNotifyRecycler?.isVisible = false
            mSettingView?.let {
                it.setOnFocusChangeListener { view, b ->
                    rlSetting.isVisible = b
                }
            }

            mWifiView?.let {
                it.setOnFocusChangeListener { view, b ->
                    rlWifi.isVisible = b
                }
            }

        }

        mHorizontalContentGrid?.addItemDecoration(
            HSlideMarginDecoration(
                resources.getDimension(R.dimen.margin_decoration_max),
                resources.getDimension(R.dimen.margin_decoration_min)
            )
        )
        mHeaderGrid?.addItemDecoration(
            HSlideMarginDecoration(
                resources.getDimension(R.dimen.margin_decoration_max),
                resources.getDimension(R.dimen.margin_decoration_min)
            )
        )
        mHeaderGrid?.pivotY = maxVerticalOffset
        mTestView?.text = "CHIHI Test Version: " + BuildConfig.VERSION_NAME
        mStoreAdapter = StoreAdapter(
            activity,
            getLayoutInflater(),
            CopyOnWriteArrayList(),
            newStoreClickCallback()
        )
        mNotifyAdapter = NotifyAdapter(
            activity,
            inflater,
            CopyOnWriteArrayList(),
            if (Config.COMPANY == 3) R.layout.holder_notify else R.layout.holder_notify_2
        )
        mMainHeaderAdapter =
            MainHeaderAdapter(activity, inflater, CopyOnWriteArrayList(), newHeaderCallback())
        mHMainContentAdapter =
            MainContentAdapter(activity, inflater, CopyOnWriteArrayList(), newContentCallback())
        mVMainContentAdapter =
            MainContentAdapter(activity, inflater, CopyOnWriteArrayList(), newContentCallback())
        mAppListAdapter = AppListAdapter(
            activity,
            getLayoutInflater(),
            CopyOnWriteArrayList(),
            R.layout.holder_app,
            newAppListCallback()
        )
        mHdmiView?.visibility = if (Config.COMPANY == 0) View.VISIBLE else View.GONE
        mGradientView?.visibility = if (Config.COMPANY == 0) View.VISIBLE else View.GONE


    }

    override fun initBefore(view: View, inflater: LayoutInflater) {
        super.initBefore(view, inflater)
        mAppBarLayout!!.addOnOffsetChangedListener(this)
        mSettingView!!.setOnClickListener(this)
        mWeatherView!!.setOnClickListener(this)
        mSearchView!!.setOnClickListener(this)
        mWifiView!!.setOnClickListener(this)
        mLoginView!!.setOnClickListener(this)
        mHelpView!!.setOnClickListener(this)
        mHdmiView!!.setOnClickListener(this)
        mGradientView!!.setOnClickListener(this)
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        fillLocal()
        fillHeader()
        mNotifyRecycler!!.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL,
                false
            )
        )
        mNotifyRecycler!!.setAdapter(mNotifyAdapter)
    }

    override fun getWallpaperView(): Int {
        return R.id.wallpaper
    }

    private fun uidPull() {
        //if (!IS_TEST) HttpRequest.uidPull(AppInfo.newInfo(getActivity()));
    }

    private fun setHeader(items: List<TypeItem>) {
        targetMenus.clear()
        targetMenus.addAll(items)
        mHeaderGrid!!.setAdapter(mMainHeaderAdapter)
        mMainHeaderAdapter!!.replace(items)
        /*mHeaderGrid?.post {
            mHeaderGrid?.requestFocus()
            mHeaderGrid!!.selectedPosition = 0
        }*/

    }

    private fun setMoviceContent(
        list: MutableList<Movice?>?,
        direction: Int,
        columns: Int,
        layoutId: Int
    ) {
        var list = list
        when (direction) {
            1 -> {
                mHorizontalContentGrid!!.setAdapter(mHMainContentAdapter)
                mHMainContentAdapter!!.setLayoutId(layoutId)
                mVerticalContentGrid!!.visibility = View.GONE
                mHorizontalContentGrid!!.visibility = View.VISIBLE
                mHMainContentAdapter!!.replace(list)
            }

            0 -> {
                if (list?.size ?: 0 > columns * 2) {
                    list = list?.subList(0, columns * 2)
                }
                mVerticalContentGrid!!.setAdapter(mVMainContentAdapter)
                mVMainContentAdapter!!.setLayoutId(layoutId)
                mVerticalContentGrid!!.visibility = View.VISIBLE
                mHorizontalContentGrid!!.visibility = View.GONE
                mVerticalContentGrid!!.setNumColumns(columns)
                activity?.let {
                    mVerticalContentGrid!!.setVerticalSpacing(
                        it.resources.getDimension(R.dimen.main_page_vertical_spacing).toInt()
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
                    if (lastWeatherTime <= 0 || TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastWeatherTime) > MAX_WEATHER_TIME) {
                        HttpRequest.getCityWeather(
                            newWeatherCallback(),
                            PreferencesManager.getCityName()
                        )
                        lastWeatherTime = System.currentTimeMillis()
                    }
                    if (TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - requestTime) >= 25) {
                        requestTime = System.currentTimeMillis()
                        requestHome()
                    }
                }
            }
        }
        exec.execute(timeRunnable)
    }

    private fun syncNotify() {
        uiHandler!!.post(Runnable {
            if (!isAdded) return@Runnable
            syncTime()
            val old = isNetworkAvailable
            isNetworkAvailable = AndroidSystem.isNetworkAvailable(activity)
            if (isNetworkAvailable != old && isNetworkAvailable) {
                uidPull()
                requestHome()
            }
            if (AndroidSystem.isEthernetConnected(activity)) {
                mWifiView!!.setImageResource(R.drawable.baseline_lan_100)
            } else {
                mWifiView!!.setImageResource(if (isNetworkAvailable) R.drawable.baseline_wifi_100 else R.drawable.baseline_wifi_off_100)
            }
            if (Config.COMPANY == 3) {
                val notifies: MutableList<Notify> = ArrayList()
                // if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) notifies.add(Notify(R.drawable.baseline_bluetooth_100))
                val deviceHashMap =
                    (activity!!.getSystemService(Context.USB_SERVICE) as UsbManager).deviceList
                for (i in 0 until deviceHashMap.size) {
                    notifies.add(Notify(R.drawable.baseline_usb_100))
                }
                if (SystemUtils.isApEnable(activity)) notifies.add(Notify(R.drawable.baseline_wifi_tethering_100_2))
                val storageManager = activity!!.getSystemService(
                    StorageManager::class.java
                )
                for (volume in storageManager.storageVolumes) {
                    if (!volume.isEmulated) notifies.add(Notify(R.drawable.baseline_sd_storage_100))
                }
                mNotifyAdapter!!.replace(notifies)
            }
        })
    }

    private fun newWeatherCallback(): ServiceRequest.Callback<WeatherData> {
        return object : ServiceRequest.Callback<WeatherData> {
            override fun onCallback(call: Call<*>, status: Int, result: WeatherData?) {
                if (!isAdded) return
                if (call.isCanceled || result == null || result.days == null || result.days.size == 0) return
                App.WEATHER.setWeather(result)
                syncWeather()
            }
        }
    }

    private fun syncWeather() {
        uiHandler!!.post(Runnable {
            if (App.WEATHER.weather == null || !isAdded) return@Runnable
            val result = App.WEATHER.weather
            mWeatherView!!.setImageBitmap(
                AndroidSystem.getImageForAssets(
                    requireContext(), FilePathMangaer.getWeatherIcon(
                        result.days[0].icon
                    )
                )
            )
        })
    }

    private fun syncTime() {
        val is24 = AppUtils.is24Display(activity)
        val calendar = Calendar.getInstance()
        val h = calendar[if (is24) Calendar.HOUR_OF_DAY else Calendar.HOUR]
        val m = calendar[Calendar.MINUTE]
        val time = getString(R.string.hour_minute_second, h, m)
        val segment = if (calendar[Calendar.AM_PM] == 0) "AM" else "PM"
        mSegmentView!!.visibility = if (is24) View.GONE else View.VISIBLE
        mSegmentView!!.text = segment
        mTimeView!!.text = time
    }

    private fun setAppContent(list: List<ApplicationInfo>) {
        mAppListAdapter!!.replace(list)
        mHorizontalContentGrid!!.setAdapter(mAppListAdapter)
        mVerticalContentGrid!!.visibility = View.GONE
        mHorizontalContentGrid!!.visibility = View.VISIBLE
    }

    private fun setProjectorContent() {
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                activity,
                getLayoutInflater(),
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
        mHorizontalContentGrid!!.setAdapter(itemBridgeAdapter)
        mVerticalContentGrid!!.visibility = View.GONE
        mHorizontalContentGrid!!.visibility = View.VISIBLE
        val list: MutableList<SettingItem?> = ArrayList()
        list.add(
            SettingItem(
                Projector.TYPE_PROJECTOR_MODE,
                getString(R.string.project_mode),
                R.drawable.baseline_model_training_100
            )
        )
        list.add(
            SettingItem(
                Projector.TYPE_SETTING,
                getString(R.string.project_crop),
                R.drawable.baseline_crop_100
            )
        )
        list.add(
            SettingItem(
                Projector.TYPE_SCREEN,
                getString(R.string.project_gradient),
                R.drawable.baseline_screenshot_monitor_100
            )
        )
        list.add(
            SettingItem(
                Projector.TYPE_HDMI,
                getString(R.string.project_hdmi),
                R.drawable.baseline_settings_input_hdmi_100
            )
        )
        arrayObjectAdapter.addAll(0, list)
    }

    private fun setToolContent() {
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                activity,
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
        mHorizontalContentGrid!!.setAdapter(itemBridgeAdapter)
        mVerticalContentGrid!!.visibility = View.GONE
        mHorizontalContentGrid!!.visibility = View.VISIBLE
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
                        startActivity(Intent(activity, ScaleScreenActivity::class.java))
                    }

                    Projector.TYPE_PROJECTOR_MODE -> {
                        val success = AndroidSystem.openProjectorMode(activity)
                        if (!success) toastInstall()
                    }

                    Projector.TYPE_HDMI -> {
                        val success = AndroidSystem.openProjectorHDMI(activity)
                        if (!success) toastInstall()
                    }

                    Projector.TYPE_SCREEN -> {
                        startActivity(Intent(activity, ChooseGradientActivity::class.java))
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
                        activity,
                        "com.mediatek.wwtv.tvcenter"
                    )

                    Tools.TYPE_FILE -> AndroidSystem.openPackageName(
                        activity,
                        "com.conocx.fileexplorer"
                    )
                }
            }
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        isExpanded = verticalOffset != 0
        val value = (1f - abs((verticalOffset / 2f).toDouble()) / maxVerticalOffset).toFloat()
        mHeaderGrid!!.scaleX = value
        mHeaderGrid!!.scaleY = value
    }

    override fun onClick(v: View) {
        if (v == mSettingView) {
            if (Config.COMPANY == 4) {
                AndroidSystem.openSystemSetting(activity)
            } else {
                startActivity(Intent(activity, SettingActivity::class.java))
            }
            //AndroidSystem.openSystemSetting(getActivity());
        } else if (v == mWeatherView) {
            startActivity(Intent(activity, WeatherActivity::class.java))
        } else if (v == mSearchView) {
            startActivity(Intent(activity, SearchActivity::class.java))
        } else if (v == mWifiView) {
            if (Config.COMPANY == 3 || Config.COMPANY == 4) {
                AndroidSystem.openWifiSetting(activity)
            } else {
                startActivity(Intent(activity, WifiListActivity::class.java))
            }
        } else if (v == mLoginView) {
            startActivity(Intent(activity, LoginActivity::class.java))
        } else if (v == mHelpView) {
            startActivity(Intent(activity, AboutActivity::class.java))
        } else if (v == mHdmiView) {
            AndroidSystem.openProjectorHDMI(activity)
        } else if (v == mGradientView) {
            //startActivity(Intent(activity, HomeGuideGroupGradientActivity::class.java))

            // startActivity(Intent(activity, ChooseGradientActivity::class.java))

            startKtxActivity<HomeGuideGroupGradientActivity>()


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
                 requireContext(),
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

    private fun fillLocal() {
        try {
            val data = Gson().fromJson(
                FileReader(
                    FilePathMangaer.getMoviePath(
                        requireContext()
                    ) + "/data/movie.json"
                ), HomeResponse.Inner::class.java
            )
            for (home in data.getMovies()) {
                for (movice in home.datas) {
                    App.MOVIE_IMAGE.put(
                        movice.url, FilePathMangaer.getMoviePath(
                            requireContext()
                        ) + "/" + movice.imageUrl
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    private fun newHeaderCallback(): MainHeaderAdapter.Callback {
        return object : MainHeaderAdapter.Callback {
            override fun onClick(bean: TypeItem) {
                when (bean.type) {
                    Types.TYPE_MOVICE -> {
                        val packages = Gson().fromJson(bean.data, Array<AppPackage>::class.java)

                        packages[0].packageName.contains("youtube").yes {
                            if(Config.COMPANY==5){
                                AndroidSystem.openPackageName(activity,"com.google.android.apps.youtube.creator")
                            }else{
                                val success = AndroidSystem.jumpPlayer(activity, packages, null)
                                if (!success) {
                                    toastInstallPKApp(bean.name, packages)
                                } else {

                                }
                            }
                        }.otherwise {
                            val success = AndroidSystem.jumpPlayer(activity, packages, null)
                            if (!success) {
                                toastInstallPKApp(bean.name, packages)
                            }
                        }


                    }

                    Types.TYPE_APP_STORE -> {
                        val success = AndroidSystem.jumpAppStore(activity)
                        if (!success) toastInstall()
                    }

                    Types.TYPE_MY_APPS -> {
                        val intent = Intent(activity, AppsActivity::class.java)
                        intent.putExtra(Atts.TYPE, bean.type)
                        startActivity(intent)
                    }
                    Types.TYPE_GOOGLE_PLAY -> {
                         AndroidSystem.openPackageName(activity,"com.android.vending")
                    }
                    Types.TYPE_MEDIA_CENTER -> {
                        AndroidSystem.openPackageName(activity,"com.explorer")

                    }
                }
            }

            override fun onSelect(selected: Boolean, bean: TypeItem) {
                Log.e("TAG", "onSelect: " + bean.type)
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
        "当前的APP名字是=====$name".d("zy1996")
        if(name==null||name==""){
            ToastUtils.show("当前APP名字是空的")
        }else{
            toast(getString(R.string.place_install, name), ToastDialog.MODE_DEFAULT, callback)

        }
    }

    private fun toast(title: String, mode: Int, callback: ToastDialog.Callback?) {
        val dialog = ToastDialog.newInstance(title, mode)
        dialog.setCallback(callback)
        dialog.show(getChildFragmentManager(), ToastDialog.TAG)
    }

    private fun fillMovice(bean: TypeItem) {
        var layoutId = R.layout.holder_content
        var columns = 1
        when (bean.layoutType) {
            1 -> {
                columns = 0
                layoutId = R.layout.holder_content
            }

            0 -> {
                columns = 4
                layoutId = R.layout.holder_content_4
            }
        }
        fillMovice(bean.id, bean.layoutType, columns, layoutId)
    }

    private fun fillMovice(id: Long, dirction: Int, columns: Int, layoutId: Int) {
        setMoviceContent(App.MOVIE_MAP[id], dirction, columns, layoutId)
        requestHome()
    }

    private fun requestHome() {
        if (isReqHome || isConnectFirst) return
        isReqHome = true
        homeCall = HttpRequest.getHomeContents(newMoviceListCallback())
    }

    private fun fillApps(replace: Boolean, isAttach: Boolean) {
        if (replace) {
            useApps.clear()
            var infos = AndroidSystem.getUserApps2(activity)
            if (infos.size > 8) {
                infos = infos.subList(0, 8)
            }
            useApps.addAll(infos)
        }
        if (isAttach) setAppContent(useApps)
    }

    private fun fillAppStore() {
        if (App.APP_STORE_ITEMS.isEmpty()) {
            val emptys: MutableList<AppItem> = ArrayList()
            try {
                val apps = Gson().fromJson(
                    FileReader(
                        FilePathMangaer.getMoviePath(
                            activity
                        ) + "/data/app.json"
                    ), Array<AppItem>::class.java
                )
                if (apps != null) {
                    for (item in apps) item.localIcon = FilePathMangaer.getMoviePath(
                        activity
                    ) + "/" + item.localIcon
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
                            if (mHeaderGrid!!.selectedPosition == -1 || mHeaderGrid!!.selectedPosition > targetMenus.size - 1 || targetMenus[mHeaderGrid!!.selectedPosition].type != Types.TYPE_APP_STORE) return
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
        mHorizontalContentGrid!!.setAdapter(mStoreAdapter)
        mVerticalContentGrid!!.visibility = View.GONE
        mHorizontalContentGrid!!.visibility = View.VISIBLE
    }

    private fun local(filePath: String, direction: Int, columns: Int, layoutId: Int) {
        val files = AndroidSystem.getAssetsFileNames(activity, filePath)
        val list: MutableList<Movice?> = ArrayList(files.size)
        for (item in files) {
            list.add(Movice(Types.TYPE_UNKNOW, null, "$filePath/$item", Movice.PIC_ASSETS))
        }
        setMoviceContent(list, direction, columns, layoutId)
    }

    private fun fillHeader() {
        try {
            val path = FilePathMangaer.getJsonPath(requireContext()) + "/Home.json"
            if (File(path).exists()) {
                val result = Gson().fromJson<HomeResponse>(
                    JsonReader(FileReader(path)),
                    HomeResponse::class.java
                )
                val header = fillData(result)
                header.addAll(items)
                addProduct5TypeItem(header)

                setHeader(header)
                if(BuildConfig.FLAVOR=="hongxin_H27002"){
                    requestFocus(mHeaderGrid,500)
                }
            } else {
                setDefault()
            }
        } catch (e: Exception) {
            setDefault()
        }
    }

    fun addProduct5TypeItem(header: MutableList<TypeItem>) {
        if(Config.COMPANY==5){
            //getString(R.string.apps)
            header.add(0,TypeItem(
                getString(R.string.apps),
                R.drawable.app_list,
                0,
                Types.TYPE_MY_APPS,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            ))
            header.add(1,TypeItem(
                "Google Play",
                R.drawable.icon_googleplay,
                0,
                Types.TYPE_GOOGLE_PLAY,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            ))
            header.add(2,TypeItem(
                "Media Center",
                R.drawable.icon_media_center,
                0,
                Types.TYPE_MEDIA_CENTER,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            ))
        }
    }

    private fun setDefault() {
        try {
            val data = Gson().fromJson(
                FileReader(
                    FilePathMangaer.getMoviePath(
                        activity
                    ) + "/data/movie.json"
                ), HomeResponse.Inner::class.java
            )
            val response = HomeResponse()
            response.data = data
            val header = fillData(response, TypeItem.TYPE_ICON_IMAGE_URL, 1)
            for (item in header) {
                item.icon = FilePathMangaer.getMoviePath(requireContext()) + "/" + item.icon
            }
            header.addAll(items)
           addProduct5TypeItem(header)
            setHeader(header)
            if(BuildConfig.FLAVOR=="hongxin_H27002"){
                requestFocus(mHeaderGrid,500)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fillData(
        result: HomeResponse,
        iconType: Int = TypeItem.TYPE_ICON_IMAGE_URL,
        imageType: Int = 0
    ): MutableList<TypeItem> {
        val homeItems = result.getData().getMovies()
        val menus: MutableList<TypeItem> = ArrayList()
        val gson = Gson()
        for (bean in homeItems) {
            val movices: MutableList<Movice?> = ArrayList(bean.datas.size)
            for (movice in bean.datas) {
                movice.picType = Movice.PIC_NETWORD
                movice.appName = bean.name
                movice.appPackage = bean.packageNames
                if (imageType == 1) {
                    val path = FilePathMangaer.getMoviePath(activity) + "/" + movice.imageUrl
                    movice.imageUrl = path
                    movice.isLocal = true
                }
                movices.add(movice)
            }
            val item = TypeItem(
                bean.name,
                bean.icon,
                UUID.randomUUID().leastSignificantBits,
                Types.TYPE_MOVICE,
                iconType,
                bean.type
            )
            item.data = gson.toJson(bean.packageNames)
            App.MOVIE_MAP.put(item.id, movices)
            if(Config.COMPANY==5){
                when(item.name){
                    "Max","Disney+","Hulu"->{

                    }
                    else->{
                        menus.add(item)
                    }
                }
            }else{
                menus.add(item)
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
                    activity,
                    Gson().toJson(bean),
                    null
                )
            }
        }
    }

    private fun newContentCallback(): MainContentAdapter.Callback {
        return object : MainContentAdapter.Callback {
            override fun onClick(bean: Movice) {
                var skip = false
                if (bean.appPackage != null) {
                    for (appPackage in bean.appPackage) {
                        if (App.SKIP_PAKS.contains(appPackage.packageName)) {
                            skip = true
                            break
                        }
                    }
                }
                var success = false
                success = if (skip) {
                    AndroidSystem.jumpVideoApp(activity, bean.appPackage, null)
                } else {
                    AndroidSystem.jumpVideoApp(activity, bean.appPackage, bean.url)
                }
                if (!success) {
                    toastInstallPKApp(bean.appName, bean.appPackage)
                }
            }

            override fun onFouces(hasFocus: Boolean, bean: Movice) {
                if (hasFocus) {
                    setExpanded(false)
                }
            }
        }
    }

    private fun toastInstallPKApp(name: String, packages: Array<AppPackage>) {
        toastInstallApp(name) { type ->
            if (type == 1) {
                val pns = arrayOfNulls<String>(packages.size)
                for (i in pns.indices) {
                    pns[i] = packages[i].packageName
                }
                AndroidSystem.jumpAppStore(activity, null, pns)
            }
        }
    }

    private fun newMoviceListCallback(): ServiceRequest.Callback<HomeResponse> {
        return object : ServiceRequest.Callback<HomeResponse> {
            override fun onCallback(call: Call<*>, status: Int, result: HomeResponse?) {
                try {
                    if (!isAdded || call.isCanceled || result == null) return
                    if (result.data == null || result.data.getMovies() == null || result.data.getMovies()
                            .isEmpty()
                    ) {
                        isConnectFirst = true
                        return
                    }
                    PreferencesUtils.setProperty(
                        Atts.LAST_UPDATE_HOME_TIME,
                        System.currentTimeMillis()
                    )
                    FileUtils.writeFile(
                        Gson().toJson(result).toByteArray(StandardCharsets.UTF_8),
                        FilePathMangaer.getJsonPath(
                            activity
                        ),
                        "Home.json"
                    )
                    val header = fillData(result)
                    header.addAll(items)

                   addProduct5TypeItem(header)

                    setHeader(header)
                    isConnectFirst = true
                    if (result.getData().reg_id != null) PreferencesUtils.setProperty(
                        Atts.RECENTLY_MODIFIED,
                        result.getData().reg_id
                    )
                    val item = header[0]
                    fillMovice(item)
                    if(BuildConfig.FLAVOR=="hongxin_H27002"){
                        requestFocus(mHeaderGrid,500)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isReqHome = false
                }
            }
        }
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
                openApp(bean)
            }

            override fun onMenuClick(bean: ApplicationInfo) {
                appMenu(bean)
            }
        }
    }

    private fun openApp(bean: ApplicationInfo) {
        AndroidSystem.openPackageName(activity, bean.packageName)
    }

    private fun appMenu(bean: ApplicationInfo) {
        val dialog = AppDialog.newInstance(bean)
        dialog.setCallback { AndroidSystem.openPackageName(activity, bean.packageName) }
        dialog.show(getChildFragmentManager(), AppDialog.TAG)
    }

    private fun setExpanded(isExpanded: Boolean) {
        mAppBarLayout!!.setExpanded(isExpanded)
    }

    private val placeholdings: List<Movice>
        private get() {
            val movices: MutableList<Movice> = ArrayList()
            for (i in 0..19) {
                movices.add(Movice(Types.TYPE_UNKNOW, null, "", Movice.PIC_PLACEHOLDING))
            }
            return movices
        }

    private fun setSpanSizeLookup(lm: GridLayoutManager, spanSizeLookup: SpanSizeLookup) {
        lm.spanSizeLookup = spanSizeLookup
    }

    private fun checkVersion() {
        checkVersion(object : ServiceRequest.Callback<VersionResponse> {
            override fun onCallback(call: Call<*>, status: Int, result: VersionResponse?) {
                if (!isAdded || call.isCanceled || result == null || result.data == null) return
                val version = result.data
                if (version.version > BuildConfig.VERSION_CODE && Config.CHANNEL == version.channel) {
                    PreferencesUtils.setProperty(Atts.UPGRADE_VERSION, version.version.toInt())
                    AndroidSystem.jumpUpgrade(activity, version)
                }
            }
        })
    }

    inner class InnerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                IntentAction.ACTION_UPDATE_WALLPAPER -> updateWallpaper()
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_REPLACED -> fillApps(
                    true,
                    mHeaderGrid!!.selectedPosition != -1 && targetMenus[mHeaderGrid!!.selectedPosition].type == Types.TYPE_MY_APPS
                )
            }
        }
    }

    inner class WallpaperReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                IntentAction.ACTION_UPDATE_WALLPAPER -> updateWallpaper()
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {}
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {}
                IntentAction.ACTION_RESET_SELECT_HOME -> if (isExpanded) requestFocus(mHeaderGrid)
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
