package com.thumbsupec.lib_base.ext.language

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.view.ContextThemeWrapper
import com.shudong.lib_base.R
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import java.util.*

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/25 17:39
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext.language
 */

fun initMultiLanguage(application: Application) {
    changeContextLocale(application)
    application.registerComponentCallbacks(object : ComponentCallbacks {
        override fun onConfigurationChanged(newConfig: Configuration) {
            changeContextLocale(application)
        }

        override fun onLowMemory() {
        }
    })
}

private var mSupportLocal: ((String) -> Locale)? = null

fun setExpandLocal(supportLocal: (String) -> Locale) {
    mSupportLocal = supportLocal
}

fun changeLanguage(context: Context, language: String): Context {
    var ctx = context
    val locale: Locale = getLocale(language)
    Locale.setDefault(locale)

    //在非application切换语言同时切换掉applicant
    if (ctx !is Application) {
        val appContext = ctx.applicationContext
        updateConfiguration(appContext, locale, language)
    }
    ctx = updateConfiguration(context, locale, language)
    val configuration = context.resources.configuration
    //兼容appcompat 1.2.0后切换语言失效问题
    return object : ContextThemeWrapper(ctx, R.style.Base_Theme_AppCompat_Empty) {
        override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
            overrideConfiguration?.setTo(configuration)
            super.applyOverrideConfiguration(overrideConfiguration)
        }
    }
}

fun changeContextLocale(context: Context): Context {
    val lang: String = (context.getSharedPreferences("multi_language", Context.MODE_PRIVATE)
        .getString("language_type", LANGUAGE_SYSTEM) ?: LANGUAGE_SYSTEM)
    return changeLanguage(context, lang)
}

fun getCurrentLanguage(): String {
    val language = Locale.getDefault().language
    val country = Locale.getDefault().country
    // 中文繁体: 港澳台
    if ("zh".equals(language, ignoreCase = true)
        && ("tw".equals(country, ignoreCase = true)
                || "hk".equals(country, ignoreCase = true)
                || "mo".equals(country, ignoreCase = true))
    ) {
        // 港澳台 使用邮箱登录
        //AppCacheBase.curLanguage = ENGLISH
        return "zh-Hant"
        //统一为简体
        //return "zh"
    }
    //印尼语特殊处理
    return if ("in".equals(language, ignoreCase = true)) {
        "id"
    } else {
        (language== CHINESE).yes {
           // AppCacheBase.curLanguage = CHINESE
        }.otherwise {
            //AppCacheBase.curLanguage = ENGLISH
        }
        language
    }
}

private fun updateConfiguration(context: Context, locale: Locale, language: String): Context {
    var ctx = context
    val resources = ctx.resources
    val configuration = resources.configuration
    val displayMetrics = resources.displayMetrics
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.setLocale(locale)
        ctx = ctx.createConfigurationContext(configuration)
    } else {
        configuration.locale = locale
    }
    resources.updateConfiguration(configuration, displayMetrics)
    ctx.getSharedPreferences("multi_language", Context.MODE_PRIVATE)
        .edit()
        .putString("language_type", language)
        .apply()
    return ctx
}

private fun getLocale(language: String): Locale {
    when (language) {
        //跟随系统
        LANGUAGE_SYSTEM -> return getLocalWithVersion(Resources.getSystem().configuration)
        //英文
        LANGUAGE_EN -> return Locale.US
        //简体中文
        LANGUAGE_ZH_CN -> return Locale.SIMPLIFIED_CHINESE
        LANGUAGE_ZH -> return Locale.SIMPLIFIED_CHINESE
        //泰语
        LANGUAGE_TH -> return Locale("th", "TH", "TH")
        //繁体中文
        LANGUAGE_ZH_TW -> return Locale.TRADITIONAL_CHINESE
        //韩语
        LANGUAGE_KO -> return Locale.KOREA
        //日语
        LANGUAGE_JA -> return Locale.JAPAN
        //阿拉伯语
        LANGUAGE_AR -> return Locale("ar", "SA")
        //印尼语
        LANGUAGE_IN -> return Locale("in", "ID")
        //西班牙语
        LANGUAGE_ES -> return Locale("es", "ES")
        //葡萄牙语
        LANGUAGE_PT -> return Locale("pt", "PT")
        //土耳其语
        LANGUAGE_TR -> return Locale("tr", "TR")
        //俄语
        LANGUAGE_RU -> return Locale("ru", "RU")
        //意大利语
        LANGUAGE_IT -> return Locale("it", "IT")
        //法语
        LANGUAGE_FR -> return Locale("fr", "FR")
        //德语
        LANGUAGE_DE -> return Locale("de", "DE")
        //越南语
        LANGUAGE_VI -> return Locale("vi", "VN")
    }
    if (mSupportLocal != null) {
        val locale = mSupportLocal!!.invoke(language)
        if (locale.language == LANGUAGE_SYSTEM) {
            return getLocalWithVersion(Resources.getSystem().configuration)
        }
        return locale
    }
    return getLocalWithVersion(Resources.getSystem().configuration)
}

private fun getLocalWithVersion(configuration: Configuration): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        configuration.locale
    }
}