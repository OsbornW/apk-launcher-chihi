package com.soya.launcher.ui.fragment

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.drake.brv.utils.addModels
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.clickNoRepeat
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.Language
import com.soya.launcher.bean.SimpleTimeZone
import com.soya.launcher.databinding.FragmentSetLanguageBinding
import com.soya.launcher.databinding.HolderLanguageBinding
import com.soya.launcher.databinding.HolderTimeZoneBinding
import java.util.Collections
import java.util.Locale

abstract class AbsLanguageFragment<VDB : FragmentSetLanguageBinding, VM : BaseViewModel> :
    BaseWallPaperFragment<VDB, VM>(), View.OnClickListener {


    override fun initView() {
        setContent()
        mBind.content.post {
            mBind.content.requestFocus()
        }

        mBind.next.setOnClickListener(this)
    }


    private var selectIndex = 0
    private fun setContent() {
        mBind.content.setup {
            addType<Language>(R.layout.holder_language)
            onBind {
                val dto = getModel<Language>()
                val binding = getBinding<HolderLanguageBinding>()
                val isSelect = modelPosition == selectIndex
                binding.check.visibility = if (isSelect) View.VISIBLE else View.GONE
                binding.desc.visibility = if (isSelect) View.GONE else View.VISIBLE
                itemView.clickNoRepeat {
                    selectIndex = modelPosition
                    onSelectLanguage(dto)
                    notifyItemRangeChanged(0, mBind.content.mutable.size)
                }
            }
        }
        val list: MutableList<Language> = ArrayList()
        mBind.content.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT)

        val locales = Resources.getSystem().assets.locales
        val strings: MutableSet<String> = HashSet()
        for (i in locales.indices) {
            val locale = Locale.forLanguageTag(locales[i])
            val language = locale.language
            val name = locale.getDisplayName(locale)
            if (!strings.contains(language) || language == "en" || language == "zh") list.add(
                Language(name, locale.displayLanguage, locale)
            )
            strings.add(language)
        }

        list.sortWith { o1, o2 -> o1.name.compareTo(o2.name) }

        var selectLauncher = 0
        var selectCountry = 0
        val locale = Locale.getDefault()
        for (i in list.indices) {
            val item = list[i].language
            if (locale.language == item.language) {
                selectLauncher = i
                if (locale.country == item.country) {
                    selectCountry = i
                }
            }
        }
        val select = if (selectCountry != 0) selectCountry else selectLauncher
        selectIndex = select
        mBind.content.addModels(list)
        mBind.content.selectedPosition = select
    }


    override fun onClick(v: View) {
        if (v == mBind.next) {
            onNext()
        }
    }

    protected fun showNext(show: Boolean) {
        mBind.next.visibility = if (show) View.VISIBLE else View.GONE
    }

    protected open fun onNext() {}

    protected open fun onSelectLanguage(bean: Language) {}
}
