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
import com.drake.brv.utils.addModels
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.UPDATE_WALLPAPER_EVENT
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.sendLiveEventData
import com.soya.launcher.App
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.FragmentChooseWallpaperBinding
import com.soya.launcher.databinding.HolderWallpaperBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.ext.clearAndNavigate
import com.soya.launcher.ext.navigateTo
import com.soya.launcher.ui.activity.MainActivity
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.utils.PreferencesUtils

class ChooseWallpaperFragment : BaseWallPaperFragment<FragmentChooseWallpaperBinding,BaseViewModel>() {


    override fun initView() {
        setContent()
        mBind.content.apply { post { requestFocus() } }
    }

   

   

    private fun setContent() {

        mBind.content.setup {
            addType<Wallpaper>(R.layout.holder_wallpaper)
            onBind {
                val dto = getModel<Wallpaper>()
                val binding = getBinding<HolderWallpaperBinding>()
                binding.root.setCallback{
                    if(it)GlideUtils.bindBlurCross( mBind.wallpaper, dto.picture, 1000)
                }
                itemView.clickNoRepeat {
                    dto.id.let { PreferencesUtils.setProperty(Atts.WALLPAPER, it) }
                    AppCache.isSkipGuid = true
                    notifyItemRangeChanged(0,mBind.content.mutable.size)
                    updateWallpaper()
                    val fragmentManager = requireActivity().supportFragmentManager

                    fragmentManager.clearAndNavigate(R.id.main_browse_fragment, MainFragment.newInstance())

                }
            }
        }


        mBind.content.setNumColumns(3)
        mBind.content.addModels(AppCache.WALLPAPERS)
        mBind.content.requestFocus()
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
