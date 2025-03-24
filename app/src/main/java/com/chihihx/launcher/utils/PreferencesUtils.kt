package com.chihihx.launcher.utils

import android.content.Context
import com.shudong.lib_base.ext.appContext

object PreferencesUtils {
    

    @JvmStatic
    fun getString(key: String?, value: String?): String? {
        if (appContext == null) {
            return null
        }
        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        return sp.getString(key, value)
    }

    @JvmStatic
    fun getInt(key: String?, value: Int): Int {
        if (appContext == null) {
            return value
        }
        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        return sp.getInt(key, value)
    }

    @JvmStatic
    fun getLong(key: String?, value: Long): Long {
        if (appContext == null) {
            return value
        }
        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        return sp.getLong(key, value)
    }

    fun getFloat(key: String?, value: Float): Float {
        if (appContext == null) {
            return value
        }
        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        return sp.getFloat(key, value)
    }

    @JvmStatic
    fun getBoolean(key: String?, value: Boolean): Boolean {
        if (appContext == null) {
            return value
        }
        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        return sp.getBoolean(key, value)
    }

    fun setProperty(key: String?, value: String?) {
        if (appContext == null) {
            return
        }

        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getProperty(key: String?, defaultValue: String?): String? {
        if (appContext == null) {
            return defaultValue
        }

        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        return sp.getString(key, defaultValue)
    }

    fun setProperty(key: String?, value: Float) {
        if (appContext == null) {
            return
        }

        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.putFloat(key, value)
        editor.commit()
    }

    fun setProperty(key: String?, value: Long) {
        if (appContext == null) {
            return
        }

        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.putLong(key, value)
        editor.commit()
    }

    fun setProperty(key: String?, value: Int) {
        if (appContext == null) {
            return
        }

        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun setProperty(key: String?, value: Boolean) {
        if (appContext == null) {
            return
        }

        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun removeProperty(key: String?) {
        if (appContext == null) {
            return
        }

        val sp = appContext.getSharedPreferences(
            appContext.packageName, Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.remove(key)
        editor.commit()
    }
}
