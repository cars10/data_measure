package com.cars10.datareport

import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.preference.*
import com.skydoves.colorpickerpreference.ColorPickerPreference

class WidgetSettingsFragment() : PreferenceFragmentCompat() {
    var appWidgetId: Int = -1

    constructor(widgetId: Int) : this() {
        appWidgetId = widgetId
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "widget_" + appWidgetId + "_settings"

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val basicSettingsCat = buildPrefCat(context, "basic_settings", "Basic settings")
        screen.addPreference(basicSettingsCat)
        basicSettingsCat.addPreference(buildDataPlanEditTextPref(context))
        basicSettingsCat.addPreference(buildDataPlanUnitListPref(context))

        val designSettingsCat = buildPrefCat(context, "design", "Design")
        screen.addPreference(designSettingsCat)
        designSettingsCat.addPreference(buildFontSizeSeekBarPref(context))
        designSettingsCat.addPreference(buildTextColorPref(context))
        designSettingsCat.addPreference(buildBackgroundColorPref(context))

        val advancedSettingsCat = buildPrefCat(context, "advanced", "Advanced")
        screen.addPreference(advancedSettingsCat)
        advancedSettingsCat.addPreference(buildShowReloadSwitchPref(context))
        advancedSettingsCat.addPreference(buildShowUpdatedSwitchPref(context))

        preferenceScreen = screen
    }

    private fun buildPrefCat(
        context: Context,
        prefKey: String,
        prefTitle: String
    ): PreferenceCategory {
        return PreferenceCategory(context).apply {
            key = prefKey
            title = prefTitle
        }
    }

    private fun buildDataPlanEditTextPref(context: Context): EditTextPreference {
        return EditTextPreference(context).apply {
            key = "data_plan"
            title = "Monthly data"
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_123_24)
            isSingleLineTitle = true
            summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()

            setDefaultValue("5")
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.setSelectAllOnFocus(true)
                editText.selectAll()
            }
        }
    }

    private fun buildDataPlanUnitListPref(context: Context): ListPreference {
        return ListPreference(context).apply {
            key = "data_plan_unit"
            title = "Set KB/MB/GB"
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_list_alt_24)
            entryValues = context.resources.getStringArray(R.array.data_plan_sizes)
            entries = context.resources.getStringArray(R.array.data_plan_sizes)
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            setDefaultValue("GB")
        }
    }

    private fun buildFontSizeSeekBarPref(context: Context): SeekBarPreference {
        return SeekBarPreference(context).apply {
            key = "font_size"
            title = "Font size"
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_format_size_24)
            min = 1
            showSeekBarValue = true
            max = 8
            setDefaultValue(3)
        }
    }

    private fun buildTextColorPref(context: Context): ColorPickerPreference {
        return ColorPickerPreference(context).apply {
            key = "text_color"
            title = context.getString(R.string.text_color)
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_color_lens_24)
            attachAlphaSlideBar = true
            attachBrightnessSlideBar = true
            positive = "confirm"
            negative = "cancel"
            cornerRadius = 26
            defaultColor = ContextCompat.getColor(context, R.color.widgetDefaultTextColor)
            onInit()
        }
    }

    private fun buildBackgroundColorPref(context: Context): ColorPickerPreference {
        return ColorPickerPreference(context).apply {
            key = "background_color"
            title = context.getString(R.string.background_color)
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_color_lens_24)
            attachAlphaSlideBar = true
            attachBrightnessSlideBar = true
            positive = "confirm"
            negative = "cancel"
            cornerRadius = 26
            defaultColor = ContextCompat.getColor(context, R.color.widgetDefaultBackgroundColor)
            onInit()
        }
    }

    private fun buildShowReloadSwitchPref(context: Context): SwitchPreferenceCompat {
        return SwitchPreferenceCompat(context).apply {
            key = "show_reload_button"
            title = context.getString(R.string.show_reload_button)
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_refresh_24)
            summary = "Will add a button to manually reload the widget."
        }
    }

    private fun buildShowUpdatedSwitchPref(context: Context): SwitchPreferenceCompat {
        return SwitchPreferenceCompat(context).apply {
            key = "show_updated_at"
            title = context.getString(R.string.show_updated_at)
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_access_time_24)
            summary = "Show when the widget was last updated."
        }
    }
}
