package com.cars10.datareport

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color

private const val PREF_DATAPLAN_KEY = "data_plan"
private const val PREF_DATAPLAN_UNIT_KEY = "data_plan_unit"
private const val PREF_SHOW_RELOAD_KEY = "show_reload_button"
private const val PREF_SHOW_UPDATED_AT_KEY = "show_updated_at"
private const val PREF_FONT_SIZE_KEY = "font_size"
private const val PREF_TEXT_COLOR_KEY = "text_color"
private const val PREF_BACKGROUND_COLOR_KEY = "background_color"

class DataUsageWidgetPrefs(private val context: Context, private val appWidgetId: Int) {
    fun dataPlan(): String {
        return getString(PREF_DATAPLAN_KEY, 500.toString())
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
        return getInt(PREF_FONT_SIZE_KEY, 2)
    }

    fun backgroundColor(): Int {
        return getInt(PREF_BACKGROUND_COLOR_KEY, Color.parseColor("#DD34454d"))
    }

    fun textColor(): Int {
        return getInt(PREF_TEXT_COLOR_KEY, Color.parseColor("#3DCADE"))
    }

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(widgetPrefsName(appWidgetId), 0)
    }

    private fun getPrefsEditable(): SharedPreferences.Editor {
        return context.getSharedPreferences(widgetPrefsName(appWidgetId), 0).edit()
    }

    private fun getString(key: String, default: String): String {
        return getPrefs().getString(key, default) ?: default
    }

    private fun getInt(key: String, default: Int): Int {
        return getPrefs().getInt(key, default)
    }

    private fun getBoolean(key: String, default: Boolean): Boolean {
        return getPrefs().getBoolean(key, default)
    }
}

fun widgetPrefsName(appWidgetId: Int): String {
    return "widget_" + appWidgetId.toString() + "_settings"
}

// Write the prefix to the SharedPreferences object for this widget
internal fun saveWidgetPrefs(
    context: Context,
    appWidgetId: Int,
    dataPlan: Int,
    dataPlanUnit: String,
    showReloadButton: Boolean,
    showUpdatedAt: Boolean,
    fontSize: Int,
    backgroundColor: Int,
    textColor: Int
) {
    val prefs = context.getSharedPreferences(widgetPrefsName(appWidgetId), 0).edit()
    prefs.putInt(PREF_DATAPLAN_KEY, dataPlan)
    prefs.putString(PREF_DATAPLAN_UNIT_KEY, dataPlanUnit)
    prefs.putBoolean(PREF_SHOW_RELOAD_KEY, showReloadButton)
    prefs.putBoolean(PREF_SHOW_UPDATED_AT_KEY, showUpdatedAt)
    prefs.putInt(PREF_FONT_SIZE_KEY, fontSize)
    prefs.putInt(PREF_BACKGROUND_COLOR_KEY, backgroundColor)
    prefs.putInt(PREF_TEXT_COLOR_KEY, textColor)
    prefs.apply()
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(widgetPrefsName(appWidgetId), 0).edit()
    prefs.remove(PREF_DATAPLAN_KEY)
    prefs.apply()
}