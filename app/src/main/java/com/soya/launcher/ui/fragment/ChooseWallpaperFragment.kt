package com.soya.launcher.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.App
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.adapter.WallpaperAdapter
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.FragmentChooseWallpaperBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.ui.activity.MainActivity
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.utils.PreferencesUtils

class ChooseWallpaperFragment : BaseWallPaperFragment<FragmentChooseWallpaperBinding,BaseViewModel>() {

    private var mArrayObjectAdapter: ArrayObjectAdapter? = null


    override fun initView() {
        setContent()
        mBind.content.apply { post { requestFocus() } }
    }

   

   

    private fun setContent() {
        mArrayObjectAdapter =
            ArrayObjectAdapter(WallpaperAdapter(requireContext(), layoutInflater, newWallpaperCallback()))
        val itemBridgeAdapter = ItemBridgeAdapter(mArrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_SMALL,
            false
        )
        mBind.content.adapter = itemBridgeAdapter
        mBind.content.setNumColumns(3)
        mArrayObjectAdapter!!.addAll(0, AppCache.WALLPAPERS)
        mBind.content.requestFocus()
    }

    private fun newWallpaperCallback(): WallpaperAdapter.Callback {
        return object : WallpaperAdapter.Callback {
            override fun onSelect(select: Boolean, bean: Wallpaper?) {
                if (select) GlideUtils.bindBlurCross( mBind.wallpaper, bean?.picture, 1000)
            }

            override fun onClick(bean: Wallpaper?) {
                bean?.id?.let { PreferencesUtils.setProperty(Atts.WALLPAPER, it) }
                AppCache.isSkipGuid = true
                mArrayObjectAdapter!!.notifyArrayItemRangeChanged(0, mArrayObjectAdapter!!.size())
                updateWallpaper()
                val fragmentManager = requireActivity().supportFragmentManager
                for (i in 0 until fragmentManager.backStackEntryCount) {
                    fragmentManager.popBackStack()
                }

                fragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, MainFragment.newInstance())
                    .commit()
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
