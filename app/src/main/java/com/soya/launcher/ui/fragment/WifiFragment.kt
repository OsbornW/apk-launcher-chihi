package com.soya.launcher.ui.fragment

import android.content.Context
import android.net.wifi.WifiManager
import androidx.core.view.isVisible
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.addModels
import com.drake.brv.utils.linear
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.NetworkType
import com.soya.launcher.bean.WifiNetwork
import com.soya.launcher.databinding.FragmentWifiBinding
import com.soya.launcher.databinding.ItemWifiBinding
import com.soya.launcher.net.viewmodel.WifiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WifiFragment : BaseWallPaperFragment<FragmentWifiBinding,WifiViewModel>() {

    private lateinit var wifiManager: WifiManager
    private var focusedSSID: String = ""


    override fun initView() {
        // 初始化 WifiManager
        wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        //setRVSaveHeight(RecyclerView.LayoutParams.WRAP_CONTENT)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { // 在 STARTED 和 RESUMED 生命周期时执行
                while (isActive) {
                    mViewModel.getFilteredNetWorks(wifiManager)
                    delay(3000)
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

            }
        }.models = arrayListOf()

        mBind.rvOtherWiFi.setup {
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

            }
        }.models = arrayListOf()

        mBind.rvOtherWiFi.apply { post { requestFocus() } }

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
        "当前的旧集合的个数是：${oldList.size}::,新集合的个数是：${newList.size}".e("chihi_error")
        if(oldList.size==0){
            rvList.addModels(newList)
        }else{
            mViewModel.updateNewData(oldList,newList,rvList,focusedSSID)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WifiFragment().apply {}
    }
}