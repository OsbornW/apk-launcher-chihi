package com.soya.launcher.ui.fragment

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.isRepeatExcute
import com.shudong.lib_base.ext.jsonToString
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.yes
import com.soya.launcher.App
import com.soya.launcher.R
import com.soya.launcher.adapter.AppItemAdapter
import com.soya.launcher.adapter.FullSearchAdapter
import com.soya.launcher.adapter.TextWatcherAdapter
import com.soya.launcher.adapter.WebAdapter
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.DivSearch
import com.soya.launcher.bean.Movice
import com.soya.launcher.bean.WebItem
import com.soya.launcher.config.Config
import com.soya.launcher.decoration.HSlideMarginDecoration
import com.soya.launcher.enums.Atts
import com.soya.launcher.enums.Types
import com.soya.launcher.http.AppServiceRequest
import com.soya.launcher.http.HttpRequest
import com.soya.launcher.http.response.AppListResponse
import com.soya.launcher.ui.dialog.KeyboardDialog
import com.soya.launcher.ui.dialog.KeyboardDialog.Companion.newInstance
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.utils.AndroidSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import retrofit2.Call
import java.util.Locale

class SearchFragment : AbsFragment(), View.OnClickListener, OnEditorActionListener,
    FullSearchAdapter.Callback {
    private var mRecommendGrid: HorizontalGridView? = null
    private var mContentGrid: VerticalGridView? = null
    private var mTitleView: TextView? = null
    private var mEditView: TextView? = null
    private var mDivSearch: View? = null

    private var mAdapter: FullSearchAdapter? = null
    private var mAppItemAdapter: AppItemAdapter? = null

    private var store: DivSearch<Any>? = null
    private var searchText: String? = null
    private var call: Call<*>? = null
    private var appCall: Call<*>? = null

    override fun onDestroy() {
        super.onDestroy()
        if (call != null) call!!.cancel()
        if (appCall != null) appCall!!.cancel()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search
    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mRecommendGrid = view.findViewById(R.id.recommend)
        mTitleView = view.findViewById(R.id.recommend_title)
        mEditView = view.findViewById(R.id.edit_query)
        mContentGrid = view.findViewById(R.id.content)
        mDivSearch = view.findViewById(R.id.div_search)

        lifecycleScope.launch {
            mAppItemAdapter = AppItemAdapter(
                activity,
                layoutInflater,
                ArrayList(),
                R.layout.holder_app_3,
                newAppClickCallback()
            )
            mAdapter = FullSearchAdapter(activity, inflater, ArrayList(), this@SearchFragment)
            mRecommendGrid?.addItemDecoration(
                HSlideMarginDecoration(
                    resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_10),
                    resources.getDimension(com.shudong.lib_dimen.R.dimen.qb_px_2)
                )
            )
        }

    }

    // 声明一个全局变量来持有协程作业
    private var debounceJob: Job? = null
    override fun initBefore(view: View, inflater: LayoutInflater) {
        super.initBefore(view, inflater)
        mDivSearch!!.setOnClickListener(this)
        mEditView!!.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

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
        })
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        lifecycleScope.launch {
            mRecommendGrid!!.adapter = mAppItemAdapter
            mContentGrid!!.adapter = mAdapter
            val word = arguments!!.getString(Atts.WORD)
            if (!TextUtils.isEmpty(word)) {
                mEditView!!.text = word
                search()
            }

            fillAppStore()
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mDivSearch)
        lifecycleScope.launch {
            delay(300)
            showKeyboard()
        }

    }

    override fun getWallpaperView(): Int {
        return R.id.wallpaper
    }

    private fun replace() {
        val list: MutableList<DivSearch<*>?> = ArrayList()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                store = DivSearch(1, getString(R.string.app_store), ArrayList<Any>(), 0)
                list.add(store)

                val pm = activity!!.packageManager
                val infos = AndroidSystem.getUserApps(activity)
                val apps: MutableList<Any?> = ArrayList()
                for (info in infos) {
                    if (info.loadLabel(pm).toString().lowercase(Locale.getDefault()).contains(
                            searchText!!.lowercase(Locale.getDefault())
                        )
                    ) {
                        apps.add(info)
                    }
                }

                if (!apps.isEmpty()) list.add(DivSearch(0, getString(R.string.app_list), apps, 3))

                val webItems: MutableList<Any?> = ArrayList()
                webItems.add(WebItem(0, "Bing", R.drawable.edge, "https://cn.bing.com"))
                webItems.add(WebItem(1, "Google", R.drawable.chrome, "https://www.google.com"))
                webItems.add(WebItem(2, "Baidu", R.drawable.baidu, "https://www.baidu.com"))
                webItems.add(WebItem(3, "Yandex", R.drawable.yandex, "https://yandex.com"))
                list.add(DivSearch(2, getString(R.string.use_website_search), webItems, 3))


            }
            withContext(Dispatchers.Main) {
                "刷新适配器1"
                mAdapter!!.replace(list)
            }
        }
        lifecycleScope.launch {

            withContext(Dispatchers.IO) {
                if (call != null) call!!.cancel()
            }
            withContext(Dispatchers.Main) {

                mContentGrid!!.visibility = View.VISIBLE
            }


            withContext(Dispatchers.IO) {
                call =
                    HttpRequest.getAppList(object : AppServiceRequest.Callback<AppListResponse?> {
                        override fun onCallback(
                            call: Call<*>?,
                            status: Int,
                            result: AppListResponse?
                        ) {
                            val request = call?.request()
                            // 打印请求方法和URL

                            // 打印 @FieldMap 参数信息
                            if (request?.method == "POST") {
                                val requestBody = request.body
                                if (requestBody is FormBody) {
                                    for (i in 0 until requestBody.size) {

                                    }
                                }
                            }

                            if (!isAdded || call?.isCanceled == true || store == null) return
                            if (result?.result == null || result.result.appList == null || result.result.appList.isEmpty()) {
                                store?.state = 1
                                lifecycleScope.launch { mAdapter!!.sync(store) }

                            } else {

                                store?.list?.addAll(result.result.appList)
                                store?.state = 2
                                lifecycleScope.launch { mAdapter!!.sync(store) }

                            }
                        }


                    }, Config.USER_ID, null, null, searchText, 1, 50)

            }



            withContext(Dispatchers.Main) {

                mAdapter!!.replace(list)
            }


        }

    }

    private fun newWebCallback(): WebAdapter.Callback {
        return WebAdapter.Callback { bean ->
            var url = bean.link
            when (bean.type) {
                0 -> url += "/search?q=$searchText"
                1 -> url += "/search?q=$searchText"
                2 -> url += "/s?wd=$searchText"
                3 -> url += "/search/?text=$searchText"
            }
            val infos = AndroidSystem.queryBrowareLauncher(activity, url)
            if (infos.isEmpty()) {
                toastInstall()
                return@Callback
            }
            val intent = AndroidSystem.openWebUrl(url)
            activity!!.startActivity(intent)
        }
    }

    private fun fillAppStore() {
        lifecycleScope.launch {
            // delay(200)
            withContext(Dispatchers.IO) {

                if (App.APP_SEARCH_STORE_ITEMS.isEmpty()) {
                    val emptys: MutableList<AppItem> = ArrayList()
                    for (i in 0..9) emptys.add(AppItem())
                    setStoreContent(emptys)
                    appCall = HttpRequest.getAppList(object :
                        AppServiceRequest.Callback<AppListResponse?> {
                        override fun onCallback(
                            call: Call<*>?,
                            status: Int,
                            result: AppListResponse?
                        ) {
                            if (!isAdded || call?.isCanceled == true || result == null || result.result == null || result.result.appList == null || result.result.appList.isEmpty()) return

                            App.APP_SEARCH_STORE_ITEMS.addAll(result.result.appList)

                            setStoreContent(App.APP_SEARCH_STORE_ITEMS)
                        }


                    }, Config.USER_ID, "81", null, null, 1, 20)
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
                mTitleView!!.text = getString(R.string.recommend_for_you, list.size)
                mAppItemAdapter!!.replace(list)
            }
        }

    }

    private fun toastInstall() {
        val dialog = ToastDialog.newInstance(getString(R.string.place_install_app))
        dialog.show(childFragmentManager, ToastDialog.TAG)
    }

    private fun newAppClickCallback(): AppItemAdapter.Callback {
        return object : AppItemAdapter.Callback {
            override fun onSelect(selected: Boolean) {
            }

            override fun onClick(bean: AppItem) {
                if (!TextUtils.isEmpty(bean.appDownLink)) AndroidSystem.jumpAppStore(
                    activity,
                    Gson().toJson(bean),
                    null
                )
            }
        }
    }

    private val placeholdings: List<Movice>
        get() {
            val movices: MutableList<Movice> = ArrayList()
            for (i in 0..19) {
                movices.add(Movice(Types.TYPE_UNKNOW, null, "empty", Movice.PIC_PLACEHOLDING))
            }
            return movices
        }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        //search()
        return false
    }

    override fun onClick(type: Int, bean: Any) {
        when (type) {
            0 -> AndroidSystem.openPackageName(activity, (bean as ApplicationInfo).packageName)
            1 -> AndroidSystem.jumpAppStore(activity, Gson().toJson(bean as AppItem), null)
            2 -> webClick(bean as WebItem)
        }
    }

    private fun search() {

        val text = mEditView!!.text.toString()
        if (call != null) call!!.cancel()
        if (!TextUtils.isEmpty(text)) {
            searchText = text
            isRepeatExcute().no {
                if (searchText?.endsWith(" ") == false) {
                    println("chenhao" + searchText!!.length)
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
        val infos = AndroidSystem.queryBrowareLauncher(activity, url)
        if (infos.isEmpty()) {
            toastInstall()
            return
        }
        val intent = AndroidSystem.openWebUrl(url)
        activity!!.startActivity(intent)
    }

    override fun onClick(v: View) {
        if (v == mDivSearch) {
            showKeyboard()
        }
    }

    private fun showKeyboard() {
        val dialog = newInstance()
        dialog.setTargetView(mEditView)
        dialog.show(childFragmentManager, KeyboardDialog.TAG)
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

