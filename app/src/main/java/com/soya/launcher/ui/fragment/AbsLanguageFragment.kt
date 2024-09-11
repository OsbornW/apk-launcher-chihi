package com.soya.launcher.ui.fragment

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.appContext
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.adapter.LanguageAdapter
import com.soya.launcher.bean.Language
import com.soya.launcher.databinding.FragmentSetLanguageBinding
import java.util.Collections
import java.util.Locale

abstract class AbsLanguageFragment<VDB : FragmentSetLanguageBinding, VM : BaseViewModel> : BaseWallPaperFragment<VDB,VM>(), View.OnClickListener {


    override fun initView() {
        setContent()
        mBind.content.post {
            mBind.content.requestFocus()
        }

        mBind.next.setOnClickListener(this)
    }




    private fun setContent() {
        val list: MutableList<Language> = ArrayList()
        val adapter = LanguageAdapter(requireContext(), LayoutInflater.from(appContext), list)
        val arrayObjectAdapter = ArrayObjectAdapter(adapter)
        val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        mBind.content.adapter = itemBridgeAdapter
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


        /*List<MyLocaleInfo> localeInfos = SystemUtils.getSystemLocaleInfos(getActivity(), false);
        for (int i = 0; i < localeInfos.size(); i++){
            Locale locale = localeInfos.get(i).getLocale();
            String name = locale.getDisplayName(locale);
            list.add(new Language(name, locale.getDisplayLanguage(), locale));
        }*/
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
        adapter.setSelect(select)

        adapter.setCallback(newCallback(arrayObjectAdapter))

        arrayObjectAdapter.addAll(0, list)
        mBind.content.selectedPosition = select
    }

    fun newCallback(adapter: ArrayObjectAdapter): LanguageAdapter.Callback {
        return LanguageAdapter.Callback { bean ->
            if (bean != null) {
                onSelectLanguage(bean)
            }
            adapter.notifyArrayItemRangeChanged(0, adapter.size())
        }
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
