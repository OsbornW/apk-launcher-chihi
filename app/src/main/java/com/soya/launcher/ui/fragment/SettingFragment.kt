package com.soya.launcher.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.soya.launcher.R
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.config.Config
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.ext.openBluetoothSettings
import com.soya.launcher.handler.PermissionHandler
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.AboutActivity
import com.soya.launcher.ui.activity.ProjectorActivity
import com.soya.launcher.ui.activity.WallpaperActivity
import com.soya.launcher.ui.activity.WifiListActivity
import com.soya.launcher.utils.AndroidSystem
import java.util.Arrays

class SettingFragment : AbsFragment() {
    private var mContentGrid: VerticalGridView? = null
    private var mTitleView: TextView? = null
    private var launcher: ActivityResultLauncher<*>? = null

    private var receiver: WallpaperReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLauncher()

        receiver = WallpaperReceiver()

        val filter1 = IntentFilter()
        filter1.addAction(IntentAction.ACTION_UPDATE_WALLPAPER)
        activity!!.registerReceiver(receiver, filter1)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(receiver)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_setting
    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mContentGrid = view.findViewById(R.id.content)
        mTitleView = view.findViewById(R.id.title)

        mTitleView?.text = getString(R.string.setting)
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        setContent(view, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mContentGrid)
    }

    override fun getWallpaperView(): Int {
        return R.id.wallpaper
    }

    private fun initLauncher() {
        launcher = PermissionHandler.createPermissionsWithIntent(
            this
        ) { }
    }

    private fun setContent(view: View, inflater: LayoutInflater) {
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                activity,
                inflater,
                newCallback(),
                R.layout.holder_setting
            )
        )
        val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_LARGE,
            false
        )
        mContentGrid!!.adapter = itemBridgeAdapter
        mContentGrid!!.setNumColumns(4)

        if (Config.COMPANY == 0) {
            arrayObjectAdapter.addAll(
                0, Arrays.asList(
                    SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    SettingItem(
                        10,
                        getString(R.string.bluetooth),
                        R.drawable.baseline_bluetooth_100
                    ),
                    SettingItem(
                        2,
                        getString(R.string.pojector),
                        R.drawable.baseline_cast_connected_100
                    ),
                    SettingItem(
                        1,
                        getString(R.string.wallpaper),
                        R.drawable.baseline_wallpaper_100
                    ),
                    SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    SettingItem(
                        4,
                        getString(R.string.date),
                        R.drawable.baseline_calendar_month_100
                    ),
                    SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)
                )
            )
        } else if (Config.COMPANY == 9) {
            arrayObjectAdapter.addAll(
                0, Arrays.asList(
                    SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    SettingItem(
                        10,
                        getString(R.string.bluetooth),
                        R.drawable.baseline_bluetooth_100
                    ),
                    SettingItem(
                        2,
                        getString(R.string.pojector),
                        R.drawable.baseline_cast_connected_100
                    ),
                    SettingItem(
                        1,
                        getString(R.string.wallpaper),
                        R.drawable.baseline_wallpaper_100
                    ),
                    SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    SettingItem(
                        4,
                        getString(R.string.date),
                        R.drawable.baseline_calendar_month_100
                    ),
                    SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)
                )
            )
        } else if (Config.COMPANY == 1) {
            arrayObjectAdapter.addAll(
                0, Arrays.asList(
                    SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    SettingItem(
                        2,
                        getString(R.string.pojector),
                        R.drawable.baseline_cast_connected_100
                    ),
                    SettingItem(
                        1,
                        getString(R.string.wallpaper),
                        R.drawable.baseline_wallpaper_100
                    ),
                    SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    SettingItem(
                        4,
                        getString(R.string.date),
                        R.drawable.baseline_calendar_month_100
                    ),
                    SettingItem(
                        5,
                        getString(R.string.bluetooth),
                        R.drawable.baseline_bluetooth_100
                    ),
                    SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)
                )
            )
        } else if (Config.COMPANY == 2) {
            arrayObjectAdapter.addAll(
                0, Arrays.asList(
                    SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    SettingItem(
                        8,
                        getString(R.string.sound),
                        R.drawable.baseline_settings_voice_100
                    ),
                    SettingItem(
                        1,
                        getString(R.string.wallpaper),
                        R.drawable.baseline_wallpaper_100
                    ),
                    SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    SettingItem(
                        4,
                        getString(R.string.date),
                        R.drawable.baseline_calendar_month_100
                    ),
                    SettingItem(
                        5,
                        getString(R.string.bluetooth),
                        R.drawable.baseline_bluetooth_100
                    ),
                    SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)
                )
            )
        } else if (Config.COMPANY == 5) {
            arrayObjectAdapter.addAll(
                0, Arrays.asList(
                    SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    SettingItem(
                        1,
                        getString(R.string.wallpaper),
                        R.drawable.baseline_wallpaper_100
                    ),
                    SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)
                )
            )
        }else if (Config.COMPANY == 7) {
            arrayObjectAdapter.addAll(
                0, Arrays.asList(
                    SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    SettingItem(
                        8,
                        getString(R.string.sound),
                        R.drawable.baseline_settings_voice_100
                    ),
                    SettingItem(
                        1,
                        getString(R.string.wallpaper),
                        R.drawable.baseline_wallpaper_100
                    ),
                    SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    SettingItem(
                        4,
                        getString(R.string.date),
                        R.drawable.baseline_calendar_month_100
                    ),
                    /*       SettingItem(
                               5,
                               getString(R.string.bluetooth),
                               R.drawable.baseline_bluetooth_100
                           ),*/
                    SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)
                )
            )
        }else {
            arrayObjectAdapter.addAll(
                0, Arrays.asList(
                    SettingItem(0, getString(R.string.network), R.drawable.baseline_wifi_100),
                    SettingItem(
                        8,
                        getString(R.string.sound),
                        R.drawable.baseline_settings_voice_100
                    ),
                    SettingItem(
                        1,
                        getString(R.string.wallpaper),
                        R.drawable.baseline_wallpaper_100
                    ),
                    SettingItem(3, getString(R.string.language), R.drawable.baseline_translate_100),
                    SettingItem(
                        4,
                        getString(R.string.date),
                        R.drawable.baseline_calendar_month_100
                    ),
                    SettingItem(9, getString(R.string.keyboard), R.drawable.baseline_keyboard_100),
                    SettingItem(6, getString(R.string.about), R.drawable.baseline_help_100),
                    SettingItem(7, getString(R.string.more), R.drawable.baseline_more_horiz_100)
                )
            )
        }
    }

    fun newCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem) {
            }

            override fun onClick(bean: SettingItem) {
                when (bean.type) {
                    0 -> if (Config.COMPANY == 3) {
                        AndroidSystem.openWifiSetting(activity)
                    } else {
                        startActivity(Intent(activity, WifiListActivity::class.java))
                    }

                    1 -> startActivity(Intent(activity, WallpaperActivity::class.java))
                    2 -> if (Config.COMPANY == 0 || Config.COMPANY == 9) {
                        startActivity(Intent(activity, ProjectorActivity::class.java))
                    } else if (Config.COMPANY == 1) {
                        AndroidSystem.openActivityName(activity, "com.qf.keystone.AllActivity")
                    }

                    3 -> product.openLanguageSetting(requireContext())
                    4 -> product.openDateSetting(requireContext())
                    5 -> if (Config.COMPANY == 1 || Config.COMPANY == 2) {
                        AndroidSystem.openBluetoothSetting2(activity)
                    } else if (Config.COMPANY == 3) {
                        AndroidSystem.openBluetoothSetting3(activity)
                    }

                    6 -> startActivity(Intent(activity, AboutActivity::class.java))
                    7 -> if (Config.COMPANY == 0 || Config.COMPANY == 1) {
                        AndroidSystem.openSystemSetting(activity)
                    } else {
                        AndroidSystem.openSystemSetting2(activity)
                    }

                    8 -> AndroidSystem.openVoiceSetting(activity)
                    9 -> AndroidSystem.openInputSetting(activity)
                    10 -> if (Config.COMPANY == 9) {
                        context!!.openBluetoothSettings()
                    } else {
                        AndroidSystem.openBluetoothSetting4(context)
                    }

                }
            }
        }
    }

    inner class WallpaperReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                IntentAction.ACTION_UPDATE_WALLPAPER -> updateWallpaper()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): SettingFragment {
            val args = Bundle()

            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
