package com.soya.launcher

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import android.text.TextUtils
import com.lzy.okgo.OkGo
import com.shudong.lib_base.ContextManager
import com.shudong.lib_base.ext.MvvmHelper
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.CacheWeather
import com.soya.launcher.bean.Movice
import com.soya.launcher.bean.MyRunnable
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.config.Config
import com.soya.launcher.enums.Atts
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.http.HttpRequest
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.ui.dialog.RemoteDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.BluetoothScannerUtils
import com.soya.launcher.utils.FileUtils
import com.soya.launcher.utils.PreferencesUtils
import com.thumbsupec.lib_base.ext.language.initMultiLanguage
import com.thumbsupec.lib_base.toast.ToastUtils
import java.util.Arrays
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class App : Application() {
    private var receiver: InnerReceiver? = null
    private var mFailDialog: RemoteDialog? = null
    private var mSuccessDialog: RemoteDialog? = null
    private var remoteRunnable: MyRunnable? = null
    private var lastRemoteTime: Long = -1
    override fun onCreate() {
        super.onCreate()
        instance = this
        OkGo.init(this)
        OkGo.getInstance().setCertificates()
        HttpRequest.init(this)
        PreferencesUtils.init(this)
        if (TextUtils.isEmpty(PreferencesUtils.getString(Atts.UID, ""))) {
            PreferencesUtils.setProperty(Atts.UID, UUID.randomUUID().toString())
        }
        BluetoothScannerUtils.init(this)
        initRemote()
        if (Config.COMPANY == 0) {
            WALLPAPERS.add(Wallpaper(3, R.drawable.wallpaper_22))
            WALLPAPERS.add(Wallpaper(0, R.drawable.wallpaper_1))
            WALLPAPERS.add(Wallpaper(1, R.drawable.wallpaper_20))
            WALLPAPERS.add(Wallpaper(2, R.drawable.wallpaper_21))
            WALLPAPERS.add(Wallpaper(4, R.drawable.wallpaper_24))
            WALLPAPERS.add(Wallpaper(5, R.drawable.wallpaper_25))
        } else {
            WALLPAPERS.add(Wallpaper(0, R.drawable.wallpaper_1))
            WALLPAPERS.add(Wallpaper(1, R.drawable.wallpaper_20))
            WALLPAPERS.add(Wallpaper(2, R.drawable.wallpaper_21))
            WALLPAPERS.add(Wallpaper(3, R.drawable.wallpaper_22))
            WALLPAPERS.add(Wallpaper(4, R.drawable.wallpaper_24))
            WALLPAPERS.add(Wallpaper(5, R.drawable.wallpaper_25))
        }
        com.hs.App.init(this)
        AndroidSystem.setEnableBluetooth(this, true)
        timeRemote()
        if (PreferencesManager.getLastVersionCode() != BuildConfig.VERSION_CODE) {
            try {
                FileUtils.copyAssets(assets, "movies", filesDir)
                PreferencesUtils.setProperty(Atts.LAST_VERSION_CODE, BuildConfig.VERSION_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
            }
        }

        MvvmHelper.init(this@App)

        ToastUtils.init(this@App)
        ContextManager.getInstance().init(this@App)
        //BRV.modelId = BR.m

        initMultiLanguage(this)

    }

    private fun timeRemote() {
        if (remoteRunnable != null) remoteRunnable!!.interrupt()
        remoteRunnable = object : MyRunnable() {
            override fun run() {
                while (!isInterrupt) {
                    if (lastRemoteTime == -1L) continue
                    if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastRemoteTime) >= 5) {
                        lastRemoteTime = System.currentTimeMillis()
                        if (mFailDialog!!.isShowing) mFailDialog!!.dismiss()
                        if (mSuccessDialog!!.isShowing) mSuccessDialog!!.dismiss()
                    }
                }
            }
        }
        exec.execute(remoteRunnable)
    }

    private fun initRemote() {
        mFailDialog = RemoteDialog(this, R.layout.dialog_blu_fail)
        mSuccessDialog = RemoteDialog(this, R.layout.dialog_blu_success)
        receiver = InnerReceiver()
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        filter.addAction(IntentAction.ACTION_NOT_SHOW_REMOTE_DIALOG)
        filter.addAction(IntentAction.ACTION_SHOW_REMOTE_DIALOG)
        registerReceiver(receiver, filter)
    }

    inner class InnerReceiver : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {

                    if (device!!.type != BluetoothDevice.DEVICE_TYPE_LE) {

                    }
                    if (!mSuccessDialog!!.isShowing && canDrawOverlays() && useRemoteDialog){
                        mSuccessDialog!!.show()
                    }
                    if (mFailDialog!!.isShowing) {
                        mFailDialog!!.dismiss()
                        mSuccessDialog!!.setName(device.name)
                        lastRemoteTime = System.currentTimeMillis()
                    }
                }

                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    if (device!!.type != BluetoothDevice.DEVICE_TYPE_LE){

                    }
                    if (!mFailDialog!!.isShowing && canDrawOverlays() && useRemoteDialog){
                        mFailDialog!!.show()
                    }
                    if (mSuccessDialog!!.isShowing) {
                        mSuccessDialog!!.dismiss()
                        mFailDialog!!.setName(device.name)
                        lastRemoteTime = System.currentTimeMillis()
                    }
                }

                IntentAction.ACTION_SHOW_REMOTE_DIALOG -> useRemoteDialog = true
                IntentAction.ACTION_NOT_SHOW_REMOTE_DIALOG -> useRemoteDialog = false
            }
        }
    }

    private fun canDrawOverlays(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    companion object {
        @JvmStatic
        var instance: App? = null
            private set
        private val exec = Executors.newCachedThreadPool()
        @JvmField
        val MOVIE_MAP: MutableMap<Long, MutableList<Movice?>> = ConcurrentHashMap()
        @JvmField
        val MOVIE_IMAGE: MutableMap<String, Any> = ConcurrentHashMap()
        @JvmField
        val WEATHER = CacheWeather()
        @JvmField
        val WALLPAPERS: MutableList<Wallpaper> = ArrayList()
        @JvmField
        val APP_STORE_ITEMS: MutableList<AppItem> = CopyOnWriteArrayList()
        @JvmField
        val APP_SEARCH_STORE_ITEMS: List<AppItem> = CopyOnWriteArrayList()
        @JvmField
        val SKIP_PAKS: Set<String> = HashSet(
            Arrays.asList(
                *arrayOf(
                    "com.hbo.hbonow",
                    "com.wbd.stream",
                    "com.hulu.plus",
                    "com.hulu.livingroomplus",
                    "com.amazon.amazonvideo.livingroom",
                    "com.amazon.avod.thirdpartyclient",
                    "com.amazon.amazonvideo.livingroom"
                )
            )
        )
        var useRemoteDialog = true
    }
}