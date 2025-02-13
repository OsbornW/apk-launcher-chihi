package com.soya.launcher.desktop.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shudong.lib_base.ext.startKtxActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.LayoutHomeTopBinding
import com.soya.launcher.ui.activity.DesktopSelectActivity
import com.soya.launcher.ui.activity.SearchActivity
import com.soya.launcher.ui.activity.SettingActivity
import com.soya.launcher.ui.activity.WifiListActivity
import com.soya.launcher.utils.AndroidSystem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomToolbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // 初始化视图
    private var binding: LayoutHomeTopBinding =
        LayoutHomeTopBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        setupListeners()
    }

    private fun setupListeners() {

        binding.wifi.let {
            it.setOnFocusChangeListener { view, b ->
                if (b) binding.rlWifi.isVisible = true else binding.rlWifi.visibility = View.INVISIBLE
            }
        }

        binding.setting.let {
            it.setOnFocusChangeListener { view, b ->
                if (b) binding.rlSetting.isVisible = true else binding.rlSetting.visibility = View.INVISIBLE
            }
        }

        binding.gradient.let {
            it.setOnFocusChangeListener { view, b ->
                if (b) binding.projection.isVisible = true else binding.projection.visibility = View.INVISIBLE
            }
        }

        // 设置点击事件
        binding.setting.setOnClickListener {
            // 处理设置按钮点击逻辑
            context.startKtxActivity<SettingActivity>()
        }

        binding.gradient.setOnClickListener {
            // 处理 gradient 按钮点击逻辑
            context.startKtxActivity<DesktopSelectActivity>()
        }

        binding.search.setOnClickListener {
            // 处理搜索按钮点击逻辑
            context.startKtxActivity<SearchActivity>()
        }
        binding.wifi.setOnClickListener {
            // 处理搜索按钮点击逻辑
            AndroidSystem.openWifiSetting(context)
        }

        // 其他逻辑设置
    }

    // 你可以定义公开的接口或方法来与外部交互
    fun setTime(time: String) {
        binding.loopTime.text = time
    }


    fun showSettings(visible: Boolean) {
        binding.rlSetting.visibility = if (visible) VISIBLE else INVISIBLE
    }

    // 可以根据需要继续添加更多的方法来控制视图

    private val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())

    var timeJob: Job? = null
    fun showCurTime(lifecycleOwner: LifecycleOwner) {
        timeJob?.cancel()
        timeJob = lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                var previousTime: String? = null
                while (true) {
                    val currentTime = timeFormat.format(Date())

                    // 只有当分钟数发生变化时才更新 UI
                    if (currentTime != previousTime) {
                        binding.loopTime.text = currentTime
                        previousTime = currentTime
                    }

                    delay(1000) // 每秒检查一次
                }
            }
        }
    }


}
