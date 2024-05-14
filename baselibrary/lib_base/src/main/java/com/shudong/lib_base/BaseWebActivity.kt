package com.shudong.lib_base

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings.LOAD_NO_CACHE
import android.webkit.WebView
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.NetworkUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.databinding.ActivityBaseWebBinding
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.i
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.thumbsupec.lib_base.ext.language.changeLanguage
import com.thumbsupec.lib_base.web.WebLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseWebActivity : BaseVMActivity<ActivityBaseWebBinding, BaseViewModel>() {
    override fun isShowTitlerBar() = true

    protected var mAgentWeb: AgentWeb? = null
    private lateinit var url: String
    private lateinit var webTitle: String
    private lateinit var webRightText: String
    private lateinit var reportId: String
    private  var jumpType = 0

    /**
     *
     * 解决打开webview导致语言切换失效问题
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WebView(this).destroy()
        //changeLanguage(appContext, AppCacheBase.curLanguage)
        super.onCreate(savedInstanceState)
    }

    companion object{
        const val INTENT_JUMP_TYPE = "jump_type"
        const val INTENT_WEB_URL = "web_url"
        const val INTENT_WEB_TITLE = "web_title"
        const val INTENT_WEB_RIGHT_TEXT = "web_right_text"
        const val INTENT_REPORT_ID = "report_id"
    }

    override fun initView() {
        jumpType = intent.getIntExtra(INTENT_JUMP_TYPE,0)
        url = intent.getStringExtra(INTENT_WEB_URL) ?: getUrl()
        webTitle = intent.getStringExtra(INTENT_WEB_TITLE) ?: ""
        webRightText = intent.getStringExtra(INTENT_WEB_RIGHT_TEXT) ?: ""
        reportId = intent.getStringExtra(INTENT_REPORT_ID) ?: ""
        webTitle.setTitlebarText()
        webRightText.isNotEmpty().yes {
            webRightText.setTitlebarRightText {

            }
        }
        initWebView()


    }



    private fun getUrl() = "https://m.jd.com/"

    private fun initWebView() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                (NetworkUtils.isAvailable()).yes {
                    withContext(Dispatchers.Main){
                        mAgentWeb = AgentWeb.with(this@BaseWebActivity)
                            .setAgentWebParent(mBind.llContainer, RelativeLayout.LayoutParams(-1, -1))
                            .useDefaultIndicator()
                            .setWebChromeClient(mWebChromeClient)
                            .setWebViewClient(mWebViewClient)
                            .setMainFrameErrorView(R.layout.layout_net_error, -1)
                            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                            .setWebLayout(WebLayout(this@BaseWebActivity))
                            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) //打开其他应用时，弹窗咨询用户是否前往其他应用
                            .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                            .createAgentWeb()
                            .ready()
                            .go(url)
                        mAgentWeb!!.agentWebSettings.webSettings.useWideViewPort = true //将图片调整到适合webview的大小
                        mAgentWeb!!.agentWebSettings.webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
                        mAgentWeb!!.agentWebSettings.webSettings.cacheMode = LOAD_NO_CACHE // 缩放至屏幕的大小
                    }
                }.otherwise {
                    runOnUiThread {
                        //showErrorToast(com.thumbsupec.lib_res.R.string.web_error_tip.stringValue())
                        mBind.rlNoData.isVisible = true
                    }

                }
            }
        }
        "当前的 URL是------${url}".i("zy1996")

    }

    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String?) {
            super.onReceivedTitle(view, title)
            webTitle.isNullOrEmpty().yes {
                title?.setTitlebarText()
            }

        }
    }

    private val mWebViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            //do you  work
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            /*isRepeatExcute().no {
                showErrorToast(com.thumbsupec.lib_res.R.string.web_error_tip.stringValue())
                mBind.rlNoData.isVisible = true
            }*/

        }
    }

}