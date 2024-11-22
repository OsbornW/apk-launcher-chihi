package com.soya.launcher.ui.fragment

import android.os.Bundle
import com.drake.brv.utils.addModels
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.UPDATE_WALLPAPER_EVENT
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.padding
import com.shudong.lib_base.ext.sendLiveEventData
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.cache.AppCache.WALLPAPERS
import com.soya.launcher.databinding.FragmentWallpaperBinding
import com.soya.launcher.databinding.HolderWallpaperBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.product.base.product
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.utils.PreferencesUtils

class WallpaperFragment : BaseWallPaperFragment<FragmentWallpaperBinding, BaseViewModel>() {

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
        mBind.content.setup {
            addType<Wallpaper>(R.layout.holder_wallpaper)
            onBind {
                val dto = getModel<Wallpaper>()
                val binding = getBinding<HolderWallpaperBinding>()
                binding.root.setCallback{
                    if(it)GlideUtils.bindBlurCross( mBind.wallpaper, dto.picture, 800)
                }
                itemView.clickNoRepeat {
                    dto.id.let { PreferencesUtils.setProperty(Atts.WALLPAPER, it) }
                    notifyItemRangeChanged(0,mBind.content.mutable.size)
                    //mArrayObjectAdapter!!.notifyArrayItemRangeChanged(0, mArrayObjectAdapter!!.size())
                    sendLiveEventData(UPDATE_WALLPAPER_EVENT,true)
                }
            }
        }

        mBind.content.setNumColumns(3)
        mBind.content.addModels(WALLPAPERS)
        mBind.content.requestFocus()
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
