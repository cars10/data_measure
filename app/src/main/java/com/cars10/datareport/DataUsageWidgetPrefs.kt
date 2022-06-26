package com.cars10.datareport

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "com.example.datausagewidget.DataUsageWidget"
private const val PREF_DATAPLAN_KEY = "widget_dataplan_"
private const val PREF_DATAPLAN_UNIT_KEY = "widget_dataplan_unit_"

class DataUsageWidgetPrefs(private val context: Context, private val appWidgetId: Int) {
    fun dataPlan(): Int {
        return getInt(PREF_DATAPLAN_KEY, 500)
    }

    fun dataPlanUnit(): String {
        return getString(PREF_DATAPLAN_UNIT_KEY, "MB")
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
    dataPlanUnit: String
) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putInt(PREF_DATAPLAN_KEY + appWidgetId, dataPlan)
    prefs.putString(PREF_DATAPLAN_UNIT_KEY + appWidgetId, dataPlanUnit)
    prefs.apply()
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_DATAPLAN_KEY + appWidgetId)
    prefs.apply()
}