package com.cars10.datameasure

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color

const val PREF_DATAPLAN_KEY = "data_plan"
const val PREF_DATAPLAN_UNIT_KEY = "data_plan_unit"
const val PREF_SHOW_UPDATED_AT_KEY = "show_updated_at"
const val PREF_FONT_SIZE_KEY = "font_size"
const val PREF_TEXT_COLOR_KEY = "text_color"
const val PREF_BACKGROUND_COLOR_KEY = "background_color"
const val PREF_SHOW_PERCENTAGE_KEY = "show_percentage"
// const val PREF_FULL_WIDTH_KEY = "full_width"

class DataUsageWidgetPrefs(private val context: Context, private val appWidgetId: Int) {
    fun dataPlan(): String {
        return getString(PREF_DATAPLAN_KEY, 500.toString())
    }

    fun dataPlanUnit(): String {
        return getString(PREF_DATAPLAN_UNIT_KEY, "MB")
    }

    fun dataPlanBytes(): Int {
        var total = dataPlan().toInt()
        val unit = dataPlanUnit()
        if (unit == "GB") {
            total *= 1024 * 1024 * 1024
        } else if (unit == "MB") {
            total *= 1024 * 1024
        }

        return total
    }

    fun showUpdatedAt(): Boolean {
        return getBoolean(PREF_SHOW_UPDATED_AT_KEY, false)
    }

    fun fontSize(): Int {
        return getInt(PREF_FONT_SIZE_KEY, 2)
    }

    fun backgroundColor(): Int {
        return getInt(
            PREF_BACKGROUND_COLOR_KEY + "_" + appWidgetId.toString(),
            Color.parseColor("#DD34454d")
        )
    }

    fun textColor(): Int {
        return getInt(
            PREF_TEXT_COLOR_KEY + "_" + appWidgetId.toString(),
            Color.parseColor("#3DCADE")
        )
    }

    fun showPercentage(): Boolean {
        return getBoolean(PREF_SHOW_PERCENTAGE_KEY, false)
    }

//    fun fullWidth(): Boolean {
//        return getBoolean(PREF_FULL_WIDTH_KEY, false)
//    }

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(widgetPrefsName(appWidgetId), 0)
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
