package com.soya.launcher.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.ui.activity.ChooseGradientActivity
import com.soya.launcher.ui.activity.ScaleScreenActivity
import com.soya.launcher.utils.AndroidSystem

class ProjectorFragment : AbsFragment() {
    private var mTitleView: TextView? = null
    private var mContentGrid: VerticalGridView? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_projector
    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mTitleView = view.findViewById(R.id.title)
        mContentGrid = view.findViewById(R.id.content)

        mTitleView?.text = getString(R.string.pojector)
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        setContent()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mContentGrid)
    }

    override fun getWallpaperView(): Int {
        return R.id.wallpaper
    }

    private fun setContent() {
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                activity,
                layoutInflater,
                newProjectorCallback(),
                R.layout.holder_setting_4
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

    private fun newProjectorCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem) {
            }

            override fun onClick(bean: SettingItem) {
                when (bean.type) {
                    Projector.TYPE_SETTING -> {
                        /*boolean success = AndroidSystem.openProjectorSetting(getActivity());
                        if (!success) Toast.makeText(getActivity(), getString(R.string.place_install_app), Toast.LENGTH_SHORT).show();*/
                        isRK3326().yes {
                            AndroidSystem.openActivityName(
                                activity,
                                "com.lei.hxkeystone",
                                "com.lei.hxkeystone.ScaleActivity"
                            )
                        }.otherwise {
                            startActivity(Intent(activity, ScaleScreenActivity::class.java))
                        }
                    }

                    Projector.TYPE_PROJECTOR_MODE -> {
                        val success = AndroidSystem.openProjectorMode(activity)
                        if (!success) Toast.makeText(
                            activity,
                            getString(R.string.place_install_app),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Projector.TYPE_HDMI -> {
                        val success = AndroidSystem.openProjectorHDMI(activity)
                        if (!success) Toast.makeText(
                            activity,
                            getString(R.string.place_install_app),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Projector.TYPE_SCREEN -> {
                        startActivity(Intent(activity, ChooseGradientActivity::class.java))
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ProjectorFragment {
            val args = Bundle()

            val fragment = ProjectorFragment()
            fragment.arguments = args
            return fragment
        }
    }
}