package com.soya.launcher.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.soya.launcher.App
import com.soya.launcher.R
import com.soya.launcher.adapter.WallpaperAdapter
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.cache.AppCache
import com.soya.launcher.enums.Atts
import com.soya.launcher.ui.activity.MainActivity
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.utils.PreferencesUtils

class ChooseWallpaperFragment : AbsFragment() {
    private var mContentGrid: VerticalGridView? = null
     private var wallpaperView: ImageView? = null

    private var mArrayObjectAdapter: ArrayObjectAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_choose_wallpaper
    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mContentGrid = view.findViewById(R.id.content)
        wallpaperView = view.findViewById(R.id.wallpaper)
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        setContent()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mContentGrid)
    }

    private fun setContent() {
        mArrayObjectAdapter =
            ArrayObjectAdapter(WallpaperAdapter(activity, layoutInflater, newWallpaperCallback()))
        val itemBridgeAdapter = ItemBridgeAdapter(mArrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_SMALL,
            false
        )
        mContentGrid!!.adapter = itemBridgeAdapter
        mContentGrid!!.setNumColumns(3)
        mArrayObjectAdapter!!.addAll(0, App.WALLPAPERS)
        mContentGrid!!.requestFocus()
    }

    private fun newWallpaperCallback(): WallpaperAdapter.Callback {
        return object : WallpaperAdapter.Callback {
            override fun onSelect(select: Boolean, bean: Wallpaper) {
                if (select) GlideUtils.bindBlurCross(activity, wallpaperView, bean.picture, 1000)
            }

            override fun onClick(bean: Wallpaper) {
                PreferencesUtils.setProperty(Atts.WALLPAPER, bean.id)
                AppCache.isSkipGuid = true
                mArrayObjectAdapter!!.notifyArrayItemRangeChanged(0, mArrayObjectAdapter!!.size())
                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, MainFragment.newInstance()).commit();
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ChooseWallpaperFragment {
            val args = Bundle()

            val fragment = ChooseWallpaperFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
