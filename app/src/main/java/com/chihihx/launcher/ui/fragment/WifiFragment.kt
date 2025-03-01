package com.chihihx.launcher.ui.fragment

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.core.view.isVisible
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.drake.brv.utils.addModels
import com.drake.brv.utils.linear
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.BaseWallPaperFragment
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.NetworkType
import com.chihihx.launcher.bean.WifiNetwork
import com.chihihx.launcher.databinding.FragmentWifiBinding
import com.chihihx.launcher.databinding.ItemWifiBinding
import com.chihihx.launcher.ext.connectToSavedWiFi
import com.chihihx.launcher.ext.connectToWiFi
import com.chihihx.launcher.ext.disconnectCurrentWifi
import com.chihihx.launcher.net.viewmodel.WifiViewModel
import com.chihihx.launcher.pop.showWifiPWDDialog
import com.chihihx.launcher.pop.showWifiSavedDialog
import com.chihihx.launcher.ui.broadcast.AppStateChangeReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WifiFragment : BaseWallPaperFragment<FragmentWifiBinding,WifiViewModel>() {

    private lateinit var wifiManager: WifiManager
    private var focusedSSID: String = ""
    private var CurSSID: String = ""

    private lateinit var wifiReceiver: AppStateChangeReceiver

    override fun onDestroy() {
        super.onDestroy()
        // 注销接收器
        requireContext().unregisterReceiver(wifiReceiver)
    }

    override fun initView() {

        wifiReceiver = AppStateChangeReceiver()

        // 注册接收器
        val wifiStateIntentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        wifiStateIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        requireContext().registerReceiver(wifiReceiver, wifiStateIntentFilter)

        // 初始化 WifiManager
        wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        //setRVSaveHeight(RecyclerView.LayoutParams.WRAP_CONTENT)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { // 在 STARTED 和 RESUMED 生命周期时执行
                while (isActive) {
                    mViewModel.getFilteredNetWorks(wifiManager)
                    delay(1000)
                }
            }
        }

        mBind.rvSavedWiFi.linear().setup {
            addType<WifiNetwork>(R.layout.item_wifi)
            onBind {
                val dto = getModel<WifiNetwork>()
                val binding = getBinding<ItemWifiBinding>()
                when(dto.networkType){
                    NetworkType.CONNECTED->{
                        binding.tvConnectedStatus.text = "已连接"
                    }
                    NetworkType.SAVED->{
                        binding.tvConnectedStatus.text = "已保存"
                    }
                    NetworkType.CONNECTING->{
                        binding.tvConnectedStatus.text = "连接中"
                    }
                    else->{
                        binding.tvConnectedStatus.text = "未知"
                    }
                }
                binding.tvConnectedWiFiName.text = dto.ssid
                itemView.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        focusedSSID = dto.ssid
                    }
                }
                itemView.clickNoRepeat {
                    val isSaved = dto.networkType==NetworkType.SAVED
                    val title = if(!isSaved)"已连接的网络" else "已保存的网络"
                    val rightText = if(!isSaved)"断开连接" else "连接"
                    val middleText = "取消保存"
                    showWifiSavedDialog {
                        textTitleData.value = title
                        textContentData.value = dto.ssid
                        textConfirmData.value = rightText
                        textCleanData.value = middleText
                        confirmAction {
                            isSaved.yes {
                                //点击保存的网络，进行连接
                               val isSusccess =  wifiManager.connectToSavedWiFi(dto.ssid)
                                "连接已保存的网络,是否成功：${isSusccess}".e("chihi_error")
                            }.otherwise {
                                //点击已连接的网络，进行断开连接
                                "断开已连接的网络".e("chihi_error")
                                wifiManager.disconnectCurrentWifi()
                            }
                        }

                    }
                }

            }
        }.models = arrayListOf()

        mBind.rvOtherWiFi.linear().setup {
            addType<WifiNetwork>(R.layout.item_wifi)
            onBind {
                val dto = getModel<WifiNetwork>()
                val binding = getBinding<ItemWifiBinding>()
                binding.tvConnectedStatus.isVisible = false
                binding.tvConnectedWiFiName.text = dto.ssid
                itemView.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        focusedSSID = dto.ssid
                    }
                }
                itemView.clickNoRepeat {
                    showWifiPWDDialog {
                        textTitleData.value = "输入 ${dto.ssid} 的密码"
                        confirmAction {
                            wifiManager.connectToWiFi(dto.ssid,dto.requiresPassword.yes { it }.otherwise { null })
                        }
                    }
                }

            }
        }.models = arrayListOf()

       // mBind.rvOtherWiFi.apply { post { requestFocus() } }

    }

    override fun initObserver() {
        mViewModel.updateSavedNetworksData.observe(this){
            updateData(it,mBind.rvSavedWiFi)
        }
        mViewModel.updateOtherNetworksData.observe(this){
            updateData(it,mBind.rvOtherWiFi)
        }
    }


    fun setRVSaveHeight(height: Int) {
        mBind.apply {
            mBind.rvSavedWiFi.post {
                mBind.rvSavedWiFi.layoutParams?.height = height
                mBind.rvSavedWiFi.requestLayout() // 强制重新布局
            }
        }
    }


    fun updateData(newList: MutableList<WifiNetwork>,rvList:VerticalGridView) {

        val oldList = rvList.mutable as MutableList<WifiNetwork>
        if(oldList.size==0){
            rvList.addModels(newList)
        }else{

            if(rvList.id==R.id.rvSavedWiFi){
                mViewModel.updateSavedData(oldList,newList,rvList,focusedSSID)

            }else{
                mViewModel.updateNewData(oldList,newList,rvList,focusedSSID)
            }
            //if(oldList.size==newList.size){
                //mViewModel.updateNewData(oldList,newList,rvList,focusedSSID)
            /*}else{
                if(rvList.id==R.id.rvSavedWiFi){
                    oldList.forEach {
                        "当前旧的集合数据：${it.ssid}：：${it.networkType.name}".e("chihi_error")
                    }
                    newList.forEach {
                        "当前新的集合数据：${it.ssid}：：${it.networkType.name}".e("chihi_error")
                    }
                }

            }*/

        }

    }


    companion object {
        @JvmStatic
        fun newInstance() =
            WifiFragment().apply {}
    }
}