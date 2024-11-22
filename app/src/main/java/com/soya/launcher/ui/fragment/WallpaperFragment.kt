package com.soya.launcher.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.UPDATE_WALLPAPER_EVENT
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.padding
import com.shudong.lib_base.ext.sendLiveEventData
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.adapter.WallpaperAdapter
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.cache.AppCache.WALLPAPERS
import com.soya.launcher.databinding.FragmentWallpaperBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.product.base.product
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.utils.PreferencesUtils
import kotlinx.serialization.json.JsonNull.content

class WallpaperFragment : BaseWallPaperFragment<FragmentWallpaperBinding, BaseViewModel>() {

    private var mArrayObjectAdapter: ArrayObjectAdapter? = null
    override val shouldSetPaddingTop: Boolean = false

    override fun initView() {
       mBind.apply {
           layout.title.text = getString(R.string.wallpaper)
       }

        val isShowTitle = product.isShowPageTitle()
        isShowTitle.no { mBind.llRoot.padding(topPadding = com.shudong.lib_dimen.R.dimen.qb_px_15.dimenValue()) }
    }

    override fun initdata() {
        setContent()
        mBind.apply {
            content.apply {
                post {
                    requestFocus()
                }
            }
        }
    }


    private fun setContent() {
        mArrayObjectAdapter =
            ArrayObjectAdapter(WallpaperAdapter(requireContext(), layoutInflater, newWallpaperCallback()))
        val itemBridgeAdapter = ItemBridgeAdapter(mArrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_MEDIUM,
            false
        )
        mBind.content.adapter = itemBridgeAdapter
        mBind.content.setNumColumns(3)
        mArrayObjectAdapter!!.addAll(0, WALLPAPERS)
        mBind.content.requestFocus()
    }

    fun newWallpaperCallback(): WallpaperAdapter.Callback {
        return object : WallpaperAdapter.Callback {
            override fun onSelect(select: Boolean, bean: Wallpaper?) {
                if (select) GlideUtils.bindBlurCross( mBind.wallpaper, bean?.picture, 800)
            }

            override fun onClick(bean: Wallpaper?) {
                bean?.id?.let { PreferencesUtils.setProperty(Atts.WALLPAPER, it) }
                mArrayObjectAdapter!!.notifyArrayItemRangeChanged(0, mArrayObjectAdapter!!.size())
                //activity!!.sendBroadcast(Intent(IntentAction.ACTION_UPDATE_WALLPAPER))
                //updateWallpaper()
                sendLiveEventData(UPDATE_WALLPAPER_EVENT,true)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): WallpaperFragment {
            val args = Bundle()

            val fragment = WallpaperFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
