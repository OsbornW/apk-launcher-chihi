package com.soya.launcher.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.role.RoleManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller.SessionParams
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.ScanResult
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.role.RoleManagerCompat
import com.google.gson.Gson
import com.open.system.SystemUtils
import com.soya.launcher.BuildConfig
import com.soya.launcher.R
import com.soya.launcher.bean.PackageName
import com.soya.launcher.bean.Version
import com.soya.launcher.config.Config
import com.soya.launcher.enums.Atts
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.ui.activity.MainActivity
import java.io.IOException
import java.util.Collections
import java.util.Locale

object AndroidSystem {
    fun isSdCardAvaiable(context: Context?): Boolean {
        val infos = SystemUtils.getVolumeInfos(context)
        return !infos.isEmpty()
    }

    fun isEthernetConnected(context: Context): Boolean {
        var cm: ConnectivityManager? = null
        cm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(ConnectivityManager::class.java)
        } else {
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        val info = cm!!.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)
        return info != null && info.isConnectedOrConnecting && info.isAvailable
    }

    fun openSystemSetting2(context: Context) {
        try {
            openActivityName(
                context,
                "com.android.tv.settings",
                "com.android.tv.settings.MainSettings"
            )
        } catch (e: Exception) {
            openSystemSetting(context)
        }
    }

    fun requestInstallApk(context: Context) {
        val packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun findPackageInfo(context: Context, packageName: String): PackageInfo? {
        val packageManager = context.packageManager
        val packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)

        for (packageInfo in packageList) {
            if (packageName == packageInfo.packageName) {
                return packageInfo
            }
        }
        return null
    }

    @JvmStatic
    fun getDeviceId(context: Context): String {
        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return androidId
    }

    fun view2Bitmap(context: Context?, dectorView: View): Bitmap {
        dectorView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(dectorView.drawingCache)
        dectorView.isDrawingCacheEnabled = false
        return bitmap
    }

    @JvmStatic
    fun blur(context: Context?, dectorView: View, root: View, blur: ImageView) {
        val bitmap = view2Bitmap(context, dectorView)
        root.post {
            val lp = blur.layoutParams
            lp.width = root.measuredWidth
            lp.height = root.measuredHeight
            val bit = Bitmap.createBitmap(lp.width, lp.height, Bitmap.Config.RGB_565)
            val rect = Rect()
            root.getGlobalVisibleRect(rect)
            blur.layoutParams = lp
            val canvas = Canvas(bit)
            canvas.drawBitmap(bitmap, rect, Rect(0, 0, bit.width, bit.height), Paint())
            canvas.drawARGB(95, 168, 168, 168)
            //bit = Bitmap.createScaledBitmap(bit, (int) (lp.width * 0.7f), (int) (lp.height * 0.7f), false);
            GlideUtils.bindBlur(context, blur, bit, blur.drawable, 12, 8)
        }
    }

    fun isUsePassWifi(bean: ScanResult?): Boolean {
        return bean?.let { AccessPointState.getScanResultSecurity(it) } !== AccessPointState.OPEN
    }

    @JvmStatic
    fun getImageForAssets(context: Context, filePath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        val am = context.assets
        try {
            val `in` = am.open(filePath!!)
            bitmap = BitmapFactory.decodeStream(`in`)
            `in`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun queryCategoryAllLauncher(context: Context): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN)
        val packageManager = context.packageManager
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
    }

    fun queryBrowareLauncher(context: Context, url: String?): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.setData(Uri.parse(url))

        val packageManager = context.packageManager
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
    }

    fun queryInstalledApps(context: Context): List<PackageInfo> {
        val packageManager = context.packageManager
        val infos = packageManager.getInstalledPackages(0)
        return infos
    }

    fun openActivityInfo(context: Context, info: ActivityInfo) {
        val intent = Intent(Intent.ACTION_MAIN)
            .setClassName(info.applicationInfo.packageName, info.name)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun getPackageNameIntent(context: Context, packageName: String?): Intent? {
        val pm = context.packageManager
        var intent = pm.getLaunchIntentForPackage(packageName!!)
        if (intent == null) intent = pm.getLeanbackLaunchIntentForPackage(packageName)
        return intent
    }

    fun openPackageName(context: Context, packageName: String?): Boolean {
        val intent = getPackageNameIntent(context, packageName)
        if (intent == null) {
            return false
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return true
        }
    }

    fun openActivityName(context: Context, packageName: String?, className: String?): Boolean {
        val intent = Intent(Intent.ACTION_MAIN)
            .setClassName(packageName!!, className!!)
        context.startActivity(intent)
        return true
    }

    fun openActivityName(context: Context, name: String): Boolean {
        var success = false
        val infos = queryCategoryAllLauncher(context)
        for (info in infos) {
            if (info.activityInfo.name == name) {
                openActivityInfo(context, info.activityInfo)
                success = true
                break
            }
        }
        return success
    }

    fun openActivityNames(context: Context, names: Array<String>): Boolean {
        var success = false
        val infos = queryCategoryAllLauncher(context)
        outside@ for (name in names) {
            for (info in infos) {
                if (info.activityInfo.name == name) {
                    openActivityInfo(context, info.activityInfo)
                    success = true
                    break@outside
                }
            }
        }
        return success
    }

    fun openApplicationDetials(context: Context, packageName: String?) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", packageName, null))
        context.startActivity(intent)
    }

    fun openWifiSetting(context: Context) {
        /*Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);*/
        openActivityName(
            context,
            "com.android.tv.settings",
            "com.android.tv.settings.connectivity.NetworkActivity"
        )
    }

    fun openBluSetting(context: Context) {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        context.startActivity(intent)
    }

    fun openWirelessSetting(context: Context) {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        context.startActivity(intent)
    }

    fun setDefaultLauncher(context: Context, launcher: ActivityResultLauncher<Any>): Boolean {
        var success = false
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val manager = context.getSystemService(RoleManager::class.java)
                launcher.launch(manager.createRequestRoleIntent(RoleManagerCompat.ROLE_HOME))
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                context.startActivity(intent)
            }
            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return success
        }
    }

    fun openDateSetting(context: Context) {
        val intent = Intent(Settings.ACTION_DATE_SETTINGS)
        context.startActivity(intent)
    }

    fun openVoiceSetting(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_SOUND_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    fun openInputSetting(context: Context) {
        /*Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        context.startActivity(intent);*/
        try {
            openActivityName(
                context,
                "com.android.tv.settings",
                "com.android.tv.settings.inputmethod.KeyboardActivity"
            )
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    fun openBluetoothSetting(context: Context) {
        openActivityName(
            context,
            "com.android.tv.settings",
            "com.android.tv.settings.bluetooth.BluetoothActivity"
        )
    }

    fun openBluetoothSetting2(context: Context) {
        openActivityName(
            context,
            "com.android.tv.settings",
            "com.android.tv.settings.accessories.AddAccessoryActivity"
        )
    }

    fun openBluetoothSetting3(context: Context) {
        openActivityName(
            context,
            "com.android.tv.settings",
            "com.android.tv.settings.bluetooth.DevicePickerActivity"
        )
    }

    fun openBluetoothSetting4(context: Context) {
        openActivityName(
            context, "com.android.tv.settings",
            "com.android.tv.settings.bluetooth.BluetoothActivity"
        )
    }


    fun openLocalSetting(context: Context): Boolean {
        val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    fun openSystemSetting(context: Context): Boolean {
        val intent = Intent(Settings.ACTION_SETTINGS)
        context.startActivity(intent)
        return true
    }

    @JvmStatic
    fun getImageFromAssetsFile(context: Context, filePath: String?): Bitmap? {
        var image: Bitmap? = null
        val am = context.resources.assets
        try {
            val `is` = am.open(filePath!!)
            image = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    fun getAssetsFileNames(context: Context, filePath: String?): Array<String>? {
        var array: Array<String>? = null
        try {
            array = context.assets.list(filePath!!)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            return array
        }
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val isNetworkAvailable = cm.activeNetworkInfo != null
        val isNetworkConnected =
            isNetworkAvailable && cm.activeNetworkInfo!!.isConnectedOrConnecting

        return isNetworkConnected
    }

    fun jumpPlayer(context: Context, packages: List<PackageName>, url: String?): Boolean {
        var success = false
        val infos = queryCategoryAllLauncher(context)
        for ((activityName, packageName) in packages) {
            for (resolveInfo in infos) {
                if (packageName == resolveInfo.activityInfo.packageName) {
                    success = true
                    val info = resolveInfo.activityInfo
                    val intent = getPackageNameIntent(context, info.packageName)
                    if (TextUtils.isEmpty(url)) {
                        //;
                        openPackageName(context, resolveInfo.activityInfo.packageName)
                    } else {
                        if (TextUtils.isEmpty(activityName)) {
                            if (intent != null) {
                                //;

                                if (isRK3326()) {
                                    openPackageName(context, resolveInfo.activityInfo.packageName)
                                } else {
                                    intent.setAction(Intent.ACTION_VIEW)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.setData(Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            }
                        } else {
                            //;

                            openPN(context, url, intent!!.component!!.packageName, activityName)
                        }
                    }
                    return success
                }
            }
        }

        return success
    }

    fun openPN(context: Context, url: String?, packageName: String?, className: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            .setClassName(packageName!!, className!!)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @JvmOverloads
    fun jumpAppStore(
        context: Context,
        data: String? = null,
        packageName: Array<String?>? = null
    ): Boolean {
        var success = false
        val infos = getAppStores(context)
        var defaultInfo: ResolveInfo? = null
        for (resolveInfo in infos) {
            if (resolveInfo.activityInfo.packageName == Config.STORE_PACKAGE_NAME) {
                if (TextUtils.isEmpty(data) && packageName == null) {
                    openActivityInfo(context, resolveInfo.activityInfo)
                } else {
                    val info = resolveInfo.activityInfo
                    val intent = Intent(Intent.ACTION_MAIN)
                        .setClassName(info.applicationInfo.packageName, Config.STORE_CLASS_NAME)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra(Atts.BEAN, data)
                    intent.putExtra(Atts.PACKAGE_NAME, packageName)
                    context.startActivity(intent)
                }
                success = true
                break
            } else {
                defaultInfo = resolveInfo
            }
        }

        if (!success && defaultInfo != null && infos.size != 0) {
            success = true
            openActivityInfo(context, infos[0].activityInfo)
        }
        return success
    }

    fun jumpUpgrade(context: Context, version: Version) {
        try {
            version.appName = context.getString(R.string.app_name)
            version.activity = MainActivity::class.java.name
            version.packageName = BuildConfig.APPLICATION_ID
            val gson = Gson()
            val info = findPackageInfo(context, Config.UPDATE_PACKAGE_NAME)
            if (info != null) {
                val intent = Intent(Intent.ACTION_MAIN).setClassName(
                    Config.UPDATE_PACKAGE_NAME,
                    Config.UPDATE_CLASS_NAME
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra(Atts.BEAN, gson.toJson(version))
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun jumpVideoApp(context: Context, packages: List<PackageName>?, url: String?): Boolean {
        var success = false
        success = packages?.let { jumpPlayer(context, it, url) } == true
        return success
    }

    fun getAppStores(context: Context): List<ResolveInfo> {
        val intent = Intent()
        intent.setAction(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_MARKET)

        val pm = context.packageManager
        val infos = pm.queryIntentActivities(intent, 0)
        return infos
    }

    fun cleanFocus(rootView: View) {
        val v = rootView.findFocus()
        v?.clearFocus()
    }

    fun isSystemApp(info: PackageInfo): Boolean {
        val isSysApp = (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1
        val isSysUpd = (info.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1
        return isSysApp or isSysUpd
    }

    fun isSystemApp(info: ResolveInfo): Boolean {
        val isSysApp =
            (info.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1
        val isSysUpd =
            (info.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1
        return isSysApp || isSysUpd
    }

    fun openProjectorSetting(context: Context): Boolean {
        return openActivityNames(
            context, arrayOf(
                "com.cptp.console.MainActivity",
                "com.softwinner.tcorrection.ScaleActivity",
            )
        )
    }

    fun openProjectorMode(context: Context): Boolean {
        return openActivityName(
            context,
            "com.android.tv.settings",
            "com.android.tv.settings.display.ProjectionActivity"
        )
    }

    fun openProjectorHDMI(context: Context): Boolean {
        if (BuildConfig.FLAVOR === "hongxin_713RK" || BuildConfig.FLAVOR === "hongxin_713RK_G") {
            return openActivityNames(
                context,
                arrayOf(
                    "com.android.rockchip.camera2",
                    "com.android.rockchip.camera2.RockchipCamera2"
                )
            )
        }
        return openActivityNames(
            context,
            arrayOf(
                "com.allwinner.hdmi.TVInputSetupActivity",
                "com.softwinner.awlivetv.MainActivity"
            )
        )
    }

    val totalInternalMemorySize: Long
        get() {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val totalBlocks = stat.blockCount.toLong()
            return totalBlocks * blockSize
        }

    fun getSystemLanguage(context: Context): String {
        var locale: Locale? = null
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            Locale.getDefault()
        }
        return locale.displayLanguage
    }

    fun getUserApps2(context: Context): List<ApplicationInfo> {
        var apps: LauncherApps? = null
        apps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(LauncherApps::class.java)
        } else {
            context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        }
        val launchers = apps!!.getActivityList(null, Process.myUserHandle())

        Collections.sort(launchers) { o1: LauncherActivityInfo, o2: LauncherActivityInfo -> (o2.firstInstallTime - o1.firstInstallTime).toInt() }

        val result: MutableList<ApplicationInfo> = ArrayList()



        for (launcher in launchers) {
            val packageName = launcher.applicationInfo.packageName
            if (packageName != BuildConfig.APPLICATION_ID) {
                result.add(launcher.applicationInfo)
            }
        }
        for (item in result) {
        }
        return result
    }

    @JvmStatic
    fun isSystemApp(flags: Int): Boolean {
        return (flags and ApplicationInfo.FLAG_SYSTEM) != 0
    }

    fun getUserApps(context: Context): List<ApplicationInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val result: MutableList<ApplicationInfo> = ArrayList()


        for (packageInfo in packages) {
            val intent = getPackageNameIntent(context, packageInfo.packageName)
            if (intent != null && packageInfo.packageName != BuildConfig.APPLICATION_ID) {
                result.add(packageInfo)
            }
        }

        Collections.sort(result, java.util.Comparator { o1, o2 ->
            var value = 0
            try {
                val info1 = pm.getPackageInfo(o1.packageName, 0)
                val info2 = pm.getPackageInfo(o2.packageName, 0)
                value = (info2.firstInstallTime - info1.firstInstallTime).toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@Comparator value
        })
        return result
    }


    fun setSystemLanguage(context: Context?, locale: Locale?) {
        SystemUtils.updateLocale(locale)
    }

    fun setLanguageReflection(context: Context?, locale: Locale?) {
        try {
            val c = Class.forName("com.android.internal.app.LocalePicker")
            val method = c.getMethod("updateLocale", Locale::class.java)
            method.invoke(null, locale)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setLanguageApp(context: Context, locale: Locale?) {
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        context.resources.updateConfiguration(configuration, null)
    }

    fun openWebUrl(url: String?): Intent {
        val intent = Intent()
        intent.setAction(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        return intent
    }

    @SuppressLint("MissingPermission")
    fun setEnableBluetooth(context: Context?, enable: Boolean) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return
        if (!bluetoothAdapter.isEnabled && enable) {
            bluetoothAdapter.enable()
        } else if (bluetoothAdapter.isEnabled && !enable) {
            bluetoothAdapter.disable()
        }
        /*if (!bluetoothAdapter.isEnabled() && enable){
            bluetoothAdapter.enable();
        }else if (bluetoothAdapter.isEnabled() && !enable){
            bluetoothAdapter.disable();
        }*/
    }

    @SuppressLint("MissingPermission")
    fun connect(context: Context?, profile: BluetoothProfile?, device: BluetoothDevice): Boolean {
        var success = false
        try {
            SystemUtils.removeBond(device)
            SystemClock.sleep(1000)
            device.createBond()
            SystemClock.sleep(1000)
            val clz = Class.forName("android.bluetooth.BluetoothHidHost")
            val connect = clz.getMethod("connect", BluetoothDevice::class.java)
            success = connect.invoke(clz.cast(profile), device) as Boolean
            SystemClock.sleep(1000)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return success
        }
    }

    @JvmStatic
    fun uninstallPackage(context: Context, packageName: String?): Boolean {
        var success = false
        try {
            val packageManger = context.packageManager
            val packageInstaller = packageManger.packageInstaller
            val params = SessionParams(SessionParams.MODE_FULL_INSTALL)
            params.setAppPackageName(packageName)
            val sessionId = packageInstaller.createSession(params)
            val sender = PendingIntent.getBroadcast(
                context,
                sessionId,
                Intent(IntentAction.ACTION_DELETE_PACKAGE),
                PendingIntent.FLAG_MUTABLE
            ).intentSender
            packageInstaller.uninstall(packageName!!, sender)
            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return success
        }
    }

    fun restoreFactory(context: Context) {
        openActivityName(
            context,
            "com.android.tv.settings",
            "com.android.tv.settings.device.storage.ResetActivity"
        )
    }
}
