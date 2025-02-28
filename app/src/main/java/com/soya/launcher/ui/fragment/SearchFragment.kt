package com.soya.launcher.ui.fragment

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.leanback.widget.HorizontalGridView
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.isRepeatExcute
import com.shudong.lib_base.ext.jsonToBean
import com.shudong.lib_base.ext.net.lifecycle
import com.shudong.lib_base.ext.no
import com.soya.launcher.App
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.DivSearch
import com.soya.launcher.bean.SearchDto
import com.soya.launcher.bean.WebItem
import com.soya.launcher.databinding.FragmentSearchBinding
import com.soya.launcher.databinding.HolderFullSearchBinding
import com.soya.launcher.decoration.HSlideMarginDecoration
import com.soya.launcher.enums.Atts
import com.soya.launcher.ext.addTextWatcher
import com.soya.launcher.net.viewmodel.SearchViewModel
import com.soya.launcher.pop.showKeyBoardDialog
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.utils.AndroidSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SearchFragment : BaseWallPaperFragment<FragmentSearchBinding, SearchViewModel>() {


    private var store: DivSearch<Any>? = null
    private var searchText: String? = null


    override fun initView() {
        lifecycleScope.launch {
            mBind.recommend.setup {
                addType<AppItem>(R.layout.item_search_apps)
            }

            mBind.content.setup {
                addType<DivSearch<*>>(R.layout.holder_full_search)
                onBind {
                    val dto = getModel<DivSearch<*>>()
                    val binding = getBinding<HolderFullSearchBinding>()

                    when (dto.type) {
                        0 -> {
                            binding.content.isVisible = true
                            binding.mask.isVisible = false
                            binding.progressBar.isVisible = false
                            setAppContent(dto as DivSearch<ApplicationInfo>, binding.content)
                        }

                        1 -> if (dto.state == 0) {
                            binding.content.isVisible = false
                            binding.mask.isVisible = false
                            binding.progressBar.isVisible = true

                        } else if (dto.state == 1) {
                            binding.content.isVisible = false
                            binding.mask.isVisible = true
                            binding.progressBar.isVisible = false

                        } else {
                            binding.content.isVisible = true
                            binding.mask.isVisible = false
                            binding.progressBar.isVisible = false
                            setAppStore(dto as DivSearch<AppItem>, binding.content)
                        }

                        2 -> {
                            binding.content.isVisible = true
                            binding.mask.isVisible = false
                            binding.progressBar.isVisible = false
                            webData(dto as DivSearch<WebItem>, binding.content)
                        }
                    }

                }
            }

            mBind.recommend.addItemDecoration(
                HSlideMarginDecoration(
                    resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_10),
                    resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_2)
                )
            )
        }
    }

    private fun webData(dto: DivSearch<WebItem>, content: HorizontalGridView) {
        content.setup {
            addType<WebItem>(R.layout.holder_web)
            onBind {
                val dto = getModel<WebItem>()
                itemView.clickNoRepeat {
                    webClick(dto)
                }
            }
        }
        content.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        content.models = dto.list
    }

    private fun setAppStore(dto: DivSearch<AppItem>, content: HorizontalGridView) {
        content.setup {
            addType<AppItem>(R.layout.item_search_apps)
        }
        content.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        content.models = dto.list
    }

    private fun setAppContent(dto: DivSearch<ApplicationInfo>, content: HorizontalGridView) {
        content.setup {
            addType<ApplicationInfo>(R.layout.item_search_apps)
        }
        content.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        content.models = dto.list
    }

    override fun initdata() {
        mBind.editQuery.apply {
            setOnFocusChangeListener { view, b ->
                if (b && !requireActivity().isFinishing) {
                    showKeyboard()
                }
            }

            onConfirmClick = {
                showKeyboard()
            }

            addTextWatcher {
                onTextChanged { s, _, _, _ ->
                    // 取消之前的防抖动任务
                    debounceJob?.cancel()

                    // 启动一个新的防抖动任务
                    debounceJob = lifecycleScope.launch {
                        delay(800)
                        if (!s.isNullOrEmpty()) {
                            search()
                        }
                    }
                }
            }

        }



        lifecycleScope.launch {
            val word = arguments!!.getString(Atts.WORD)
            if (!TextUtils.isEmpty(word)) {
                mBind.editQuery.setText(word)
                search()
            }

            fillAppStore()
        }

        mBind.editQuery.apply { post { requestFocus() } }

    }


    // 声明一个全局变量来持有协程作业
    private var debounceJob: Job? = null


    var searchJob: Job? = null
    private fun replace() {
        val list: MutableList<DivSearch<*>?> = ArrayList()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                store = DivSearch(1, getString(R.string.app_store), ArrayList<Any>(), 0)
                list.add(store)

                val pm = activity!!.packageManager
                val infos = AndroidSystem.getUserApps(requireContext())
                val apps: MutableList<Any?> = ArrayList()
                for (info in infos) {
                    if (info.loadLabel(pm).toString().lowercase(Locale.getDefault()).contains(
                            searchText!!.lowercase(Locale.getDefault())
                        )
                    ) {
                        apps.add(info)
                    }
                }

                if (apps.isNotEmpty()) list.add(DivSearch(0, getString(R.string.app_list), apps, 3))

                val webItems: MutableList<Any?> = ArrayList()
                webItems.add(WebItem(0, "Bing", R.drawable.edge, "https://cn.bing.com"))
                webItems.add(WebItem(1, "Google", R.drawable.chrome, "https://www.google.com"))
                webItems.add(WebItem(2, "Baidu", R.drawable.baidu, "https://www.baidu.com"))
                webItems.add(WebItem(3, "Yandex", R.drawable.yandex, "https://yandex.com"))
                list.add(DivSearch(2, getString(R.string.use_website_search), webItems, 3))


            }
            withContext(Dispatchers.Main) {
                mBind.content.models = list
            }
        }
        lifecycleScope.launch {


            withContext(Dispatchers.Main) {

                mBind.content.visibility = View.VISIBLE
            }

            searchJob?.cancel()
            searchJob = mViewModel.reqSearchAppList(mBind.editQuery.text.toString())
                .lifecycle(this@SearchFragment, {
                }) {
                    val dto = this.jsonToBean<SearchDto>()
                    //"当前获取的数据是：${dto.code}==${dto.msg}==${dto.result?.appList?.size}".e("chihi_error")
                    if ((dto.result?.appList?.size ?: 0) > 0) {

                        dto.result?.appList?.let { store?.list?.addAll(it) }
                        store?.state = 2
                        lifecycleScope.launch {
                            val dataList = mBind.content.mutable as MutableList<DivSearch<*>>
                            val index = dataList.indexOf(store!!)
                            if (index != -1) mBind.content.adapter?.notifyItemChanged(index)
                        }
                    } else {
                        store?.state = 1
                        lifecycleScope.launch {
                            val dataList = mBind.content.mutable as MutableList<DivSearch<*>>
                            val index = dataList.indexOf(store!!)
                            if (index != -1) mBind.content.adapter?.notifyItemChanged(index)
                        }
                    }

                }

            withContext(Dispatchers.Main) {
                mBind.content.models = list
            }


        }

    }


    private fun fillAppStore() {
        lifecycleScope.launch {
            // delay(200)
            withContext(Dispatchers.IO) {

                if (App.APP_SEARCH_STORE_ITEMS.isEmpty()) {

                    mViewModel.reqSearchAppList(
                        mBind.editQuery.text.toString(),
                        "81", 20
                    )
                        .lifecycle(this@SearchFragment, {
                        }) {
                            val dto = this.jsonToBean<SearchDto>()
                            //"当前获取的数据是：${dto.code}==${dto.msg}==${dto.result?.appList?.size}".e("chihi_error")
                            if ((dto.result?.appList?.size ?: 0) > 0) {
                                dto.result?.appList?.let { App.APP_SEARCH_STORE_ITEMS.addAll(it) }

                                setStoreContent(App.APP_SEARCH_STORE_ITEMS)
                            }

                        }

                } else {
                    setStoreContent(App.APP_SEARCH_STORE_ITEMS)
                }


            }
        }


    }

    @SuppressLint("StringFormatInvalid")
    private fun setStoreContent(list: List<AppItem>) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                mBind.recommendTitle.text = getString(R.string.recommend_for_you, list.size)
                mBind.recommend.models = list
            }
        }

    }

    private fun toastInstall() {
        val dialog = ToastDialog.newInstance(getString(R.string.place_install_app))
        dialog.show(childFragmentManager, ToastDialog.TAG)
    }


    private fun search() {

        val text = mBind.editQuery.text.toString()
        if (!TextUtils.isEmpty(text)) {
            searchText = text
            isRepeatExcute().no {
                if (searchText?.endsWith(" ") == false) {
                    replace()
                }
            }

        }
    }

    private fun webClick(bean: WebItem) {
        var url = bean.link
        when (bean.type) {
            0 -> url += "/search?q=$searchText"
            1 -> url += "/search?q=$searchText"
            2 -> url += "/s?wd=$searchText"
            3 -> url += "/search/?text=$searchText"
        }
        val infos = AndroidSystem.queryBrowareLauncher(requireContext(), url)
        if (infos.isEmpty()) {
            toastInstall()
            return
        }
        val intent = AndroidSystem.openWebUrl(url)
        activity!!.startActivity(intent)
    }


    private fun showKeyboard() {
        showKeyBoardDialog {
            editTextData.value = mBind.editQuery
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(word: String?): SearchFragment {
            val args = Bundle()
            args.putString(Atts.WORD, word)
            val fragment = SearchFragment()
            fragment.arguments = args
            return fragment
        }
    }

}

