package com.soya.launcher.net.viewmodel

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.DeviceUtils
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.showErrorToast
import com.shudong.lib_base.ext.showSuccessToast
import com.shudong.lib_base.ext.stringValue
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.App
import com.soya.launcher.BuildConfig
import com.soya.launcher.PACKAGE_NAME_AUTO_RESPONSE
import com.soya.launcher.PACKAGE_NAME_IMAGE_MODE
import com.soya.launcher.R
import com.soya.launcher.bean.AuthParamsDto
import com.soya.launcher.bean.Data
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.PackageName
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.bean.UpdateDto
import com.soya.launcher.config.Config
import com.soya.launcher.enums.Atts
import com.soya.launcher.enums.Types
import com.soya.launcher.ext.SYSTEM_PROPERTY_AUTO_RESPONSE
import com.soya.launcher.ext.autoResponseText
import com.soya.launcher.ext.getMacAddress
import com.soya.launcher.ext.openApp
import com.soya.launcher.ext.setAutoResponseProperty
import com.soya.launcher.ext.systemPropertyValueBoolean
import com.soya.launcher.net.repository.AuthRepository
import com.soya.launcher.net.repository.HomeRepository
import com.soya.launcher.p50.AUTO_ENTRY
import com.soya.launcher.p50.AUTO_FOCUS
import com.soya.launcher.p50.setFunction
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.AppsActivity
import com.soya.launcher.ui.activity.MainActivity
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.MacTool
import com.soya.launcher.utils.toTrim
import com.thumbsupec.lib_base.toast.ToastUtils
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/2 16:43
 * @PACKAGE_NAME:  com.thumbsupec.ispruz.home.viewmodel
 */
class HomeViewModel : BaseViewModel() {

    private val repository: HomeRepository by inject()
    private val authRepository: AuthRepository by inject()
    //private val repositoryLocal: HomeLocalRepository by inject()

    val firmwareModelMacData = MutableLiveData<Pair<String, String>>()


    fun reqUpdateInfo(): Flow<MutableList<UpdateAppsDTO>> = repository.reqUpdateInfo()
    fun reqHomeInfo(): Flow<HomeInfoDto> = repository.reqHomeInfo()

    fun reqLauncherVersionInfo(): Flow<UpdateDto> = repository.reqVersionInfo(
        hashMapOf(
            "appId" to BuildConfig.APP_ID,
            "channel" to BuildConfig.CHANNEL,
            "chihi_type" to BuildConfig.CHIHI_TYPE,
            "version" to BuildConfig.VERSION_CODE.toString(),
            "sdk" to Build.VERSION.SDK_INT.toString(),
            "uuid" to DeviceUtils.getUniqueDeviceId(),
            "model" to BuildConfig.MODEL,
            "brand" to Build.BRAND,
            "product" to Build.PRODUCT,
            "mac" to (getMacAddress()?:""),
        )
    )

    fun reqCheckActiveCode(activeCode: String): Flow<String> = authRepository.reqCheckActiveCode(
        AuthParamsDto(activeCode.replace('-', ' ').toTrim())
    )

    fun reqSearchAppList(
        keyWords: String = "",
        appColumnId: String = "",
        pageSize: Int = 50,
        tag:String = ""
    ): Flow<String> = repository.reqSearchAppList(
        hashMapOf("userId" to 62, "keyword" to keyWords, "appColumnId" to appColumnId,"tag" to tag),
        hashMapOf("pageNo" to 1, "pageSize" to pageSize,"channel" to BuildConfig.CHANNEL),
    )




    fun handleContentClick(bean: Data,successInvoke:(isSuccess:Boolean)->Unit) {
        val skip = bean.packageNames?.any { appPackage ->
            App.SKIP_PAKS.contains(appPackage.packageName)
        } ?: false


        val success = if (skip) {
            currentActivity?.let { AndroidSystem.jumpVideoApp(it, bean.packageNames, null) }
        } else {
            currentActivity?.let { AndroidSystem.jumpVideoApp(it, bean.packageNames, bean.url) }
        }?:false
        successInvoke.invoke(success)
        //if (!success) {
            //toastInstallPKApp(bean.appName ?: "", bean.packageNames)
        //}
    }

    fun clickProjectorItem(bean: SettingItem,callback:((Pair<Int,Any>)->Unit)?=null) {
        when (bean.type) {
            Projector.TYPE_SCREEN_ZOOM -> {
                product.openScreenZoom()
            }

            Projector.TYPE_PROJECTOR_MODE -> {
                product.openProjectorMode {
                    //if (!it) toastInstall()
                }
            }

            Projector.TYPE_HDMI -> {
                val success = currentActivity?.let { AndroidSystem.openProjectorHDMI(it) }
                //if (!success) toastInstall()
            }

            Projector.TYPE_KEYSTONE_CORRECTION -> {
                product.openKeystoneCorrectionOptions()
            }
            Projector.TYPE_AUTO_RESPONSE->{
                //自动响应
                val statusText = setAutoResponseProperty()
                callback?.invoke(Pair(Projector.TYPE_AUTO_RESPONSE,statusText))
                //PACKAGE_NAME_AUTO_RESPONSE.openApp()
            }
            Projector.TYPE_AUTO_FOCUS-> setFunction(AUTO_FOCUS){
                }
            Projector.TYPE_AUTO_ENTRY-> setFunction(AUTO_ENTRY){
                }
            Projector.TYPE_IMAGE_MODE -> {
                PACKAGE_NAME_IMAGE_MODE.openApp()
            }
        }
    }

    fun handleActiveCodeData(statusCode:Long,msg:String?){
        (msg == null).no {
            ToastUtils.show(msg)
            AppCacheBase.isActive = false
            (currentActivity as MainActivity).switchAuthFragment()
        }.otherwise {
            when (statusCode) {
                10000L -> {
                    AppCacheBase.isActive = true
                }

                10004L -> {
                    ToastUtils.show("Invalid PIN, please try again! ")
                    AppCacheBase.isActive = false
                    (currentActivity as MainActivity).switchAuthFragment()
                }

                else -> {
                    ToastUtils.show("Failed, please try again!")
                    AppCacheBase.isActive = false
                    (currentActivity as MainActivity).switchAuthFragment()
                }
            }
        }
    }

    fun addAppStatusBroadcast(receiver: BroadcastReceiver){
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addDataScheme("package")
        currentActivity?.registerReceiver(receiver, filter)
    }

    fun removeAppStatusBroadcast(receiver: BroadcastReceiver){
        currentActivity?.unregisterReceiver(receiver)
    }

    fun clickHeaderItem(bean:TypeItem,toastCallback:(name:String?,packages:List<PackageName>?)->Unit){
        when (bean.type) {
            Types.TYPE_MOVICE -> {
                val packages = bean.data
                //"当前名字是====${packages.size}===${packages[0].packageName}"
                val packagesBean = packages?.get(0)
                when {
                    packagesBean?.packageName?.contains("com.amazon.amazonvideo.livingroom") == true -> {

                        if (Config.COMPANY == 5) {
                            currentActivity?.let {
                                AndroidSystem.openActivityName(
                                    it,
                                    "com.amazon.avod.thirdpartyclient",
                                    "com.amazon.avod.thirdpartyclient.LauncherActivity"
                                )
                            }

                        } else {
                            val success =
                                currentActivity?.let {
                                    AndroidSystem.jumpPlayer(
                                        it,
                                        packages, null)
                                }
                            if (!success!!) {
                                toastCallback.invoke(bean.name,packages)
                            } else {

                            }
                        }
                    }

                    packagesBean?.packageName?.contains("youtube") == true -> {

                        if (Config.COMPANY == 5) {
                            currentActivity?.let {
                                AndroidSystem.openPackageName(
                                    it,
                                    "com.google.android.apps.youtube.creator"
                                )
                            }
                        } else {
                            val success =
                                currentActivity?.let { AndroidSystem.jumpPlayer(it, packages, null) }
                            if (!success!!) {
                                toastCallback.invoke(bean.name,packages)
                            } else {

                            }
                        }
                    }

                    else -> {
                        try {
                            val success =
                                packages?.let {
                                    currentActivity?.let { it1 ->
                                        AndroidSystem.jumpPlayer(
                                            it1,
                                            it, null)
                                    }
                                }
                            if (!success!!) {
                                toastCallback.invoke(bean.name,packages)

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            //ToastUtils.show("")
                        }
                    }
                }


            }

            Types.TYPE_APP_STORE -> {
                val success = currentActivity?.let { AndroidSystem.jumpAppStore(it) }
                if (!success!!){
                    toastCallback.invoke(null,null)
                }
            }

            Types.TYPE_MY_APPS -> {
                val intent = Intent(currentActivity, AppsActivity::class.java)
                intent.putExtra(Atts.TYPE, bean.type)
                currentActivity?.startActivity(intent)
            }

            Types.TYPE_GOOGLE_PLAY -> {
                currentActivity?.let { AndroidSystem.openPackageName(it, "com.android.vending") }
            }

            Types.TYPE_MEDIA_CENTER -> {
                currentActivity?.let { AndroidSystem.openPackageName(it, "com.explorer") }

            }
        }
    }

}