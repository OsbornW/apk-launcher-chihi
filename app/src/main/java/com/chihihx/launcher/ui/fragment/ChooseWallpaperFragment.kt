package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import com.drake.brv.utils.addModels
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.chihihx.launcher.BaseWallPaperFragment
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.Wallpaper
import com.chihihx.launcher.cache.AppCache
import com.chihihx.launcher.databinding.FragmentChooseWallpaperBinding
import com.chihihx.launcher.databinding.HolderWallpaperBinding
import com.chihihx.launcher.enums.Atts
import com.chihihx.launcher.ext.clearAndNavigate
import com.chihihx.launcher.utils.GlideUtils
import com.chihihx.launcher.utils.PreferencesUtils

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
