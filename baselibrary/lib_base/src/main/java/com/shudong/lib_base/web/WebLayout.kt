package com.thumbsupec.lib_base.web

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import com.just.agentweb.IWebLayout
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.shudong.lib_base.R

/**
 * Created by cenxiaozhong on 2017/7/1.
 * source code  https://github.com/Justson/AgentWeb
 */
class WebLayout(mActivity: Activity) : IWebLayout<WebView?, ViewGroup?> {
    private val mTwinklingRefreshLayout: TwinklingRefreshLayout
    private var mWebView: WebView? = null

    init {
        mTwinklingRefreshLayout = LayoutInflater.from(mActivity)
            .inflate(R.layout.fragment_twk_web, null) as TwinklingRefreshLayout
        mTwinklingRefreshLayout.setPureScrollModeOn()
        mWebView = mTwinklingRefreshLayout.findViewById(R.id.webView)
    }

    override fun getLayout(): ViewGroup {
        return mTwinklingRefreshLayout
    }

    override fun getWebView(): WebView? {
        return mWebView
    }
}