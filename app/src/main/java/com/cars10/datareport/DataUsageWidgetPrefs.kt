package com.cars10.datareport

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "com.example.datausagewidget.DataUsageWidget"
private const val PREF_DATAPLAN_KEY = "widget_dataplan_"
private const val PREF_DATAPLAN_UNIT_KEY = "widget_dataplan_unit_"
private const val PREF_SHOW_RELOAD_KEY = "widget_show_reload_"
private const val PREF_SHOW_UPDATED_AT_KEY = "widget_show_updated_at_"
private const val PREF_FONT_SIZE_KEY = "widget_font_size"

class DataUsageWidgetPrefs(private val context: Context, private val appWidgetId: Int) {
    fun dataPlan(): Int {
        return getInt(PREF_DATAPLAN_KEY, 500)
    }

    fun dataPlanUnit(): String {
        return getString(PREF_DATAPLAN_UNIT_KEY, "MB")
    }

    fun showReload(): Boolean {
        return getBoolean(PREF_SHOW_RELOAD_KEY, false)
    }

    fun showUpdatedAt(): Boolean {
        return getBoolean(PREF_SHOW_UPDATED_AT_KEY, false)
    }

    fun fontSize(): Int {
        return getInt(PREF_FONT_SIZE_KEY, 3)
    }

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, 0)
    }

    private fun getPrefsEditable(): SharedPreferences.Editor {
        return context.getSharedPreferences(PREFS_NAME, 0).edit()
    }

    private fun getString(key: String, default: String): String {
        return getPrefs().getString(key + appWidgetId, default) ?: default
    }

    private fun getInt(key: String, default: Int): Int {
        return getPrefs().getInt(key + appWidgetId, default)
    }

    private fun getBoolean(key: String, default: Boolean): Boolean {
        return getPrefs().getBoolean(key + appWidgetId, default)
    }

    fun delete(key: String) {
        val prefs = getPrefsEditable()
        prefs.remove(PREF_DATAPLAN_KEY + appWidgetId)
        prefs.apply()
    }
}

// Write the prefix to the SharedPreferences object for this widget
internal fun saveWidgetPrefs(
    context: Context,
    appWidgetId: Int,
    dataPlan: Int,
    dataPlanUnit: String,
    showReloadButton: Boolean,
    showUpdatedAt: Boolean,
    fontSize: Int
) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putInt(PREF_DATAPLAN_KEY + appWidgetId, dataPlan)
    prefs.putString(PREF_DATAPLAN_UNIT_KEY + appWidgetId, dataPlanUnit)
    prefs.putBoolean(PREF_SHOW_RELOAD_KEY + appWidgetId, showReloadButton)
    prefs.putBoolean(PREF_SHOW_UPDATED_AT_KEY + appWidgetId, showUpdatedAt)
    prefs.putInt(PREF_FONT_SIZE_KEY + appWidgetId, fontSize)
    prefs.apply()
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_DATAPLAN_KEY + appWidgetId)
    prefs.apply()
}