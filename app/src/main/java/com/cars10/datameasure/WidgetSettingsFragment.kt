package com.cars10.datameasure

import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.preference.*
import com.skydoves.colorpickerpreference.ColorPickerPreference

class WidgetSettingsFragment : PreferenceFragmentCompat() {
    var appWidgetId: Int = -1

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = widgetPrefsName(appWidgetId)

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val basicSettingsCat =
            buildPrefCat(context, "basic_settings", context.getString(R.string.basic_settings))
        screen.addPreference(basicSettingsCat)
        basicSettingsCat.addPreference(buildDataPlanEditTextPref(context))
        basicSettingsCat.addPreference(buildDataPlanUnitListPref(context))

        val designSettingsCat =
            buildPrefCat(context, "design", context.getString(R.string.design_settings))
        screen.addPreference(designSettingsCat)
        designSettingsCat.addPreference(buildFontSizeSeekBarPref(context))
        designSettingsCat.addPreference(buildTextColorPref(context))
        designSettingsCat.addPreference(buildBackgroundColorPref(context))

        val advancedSettingsCat =
            buildPrefCat(context, "advanced", context.getString(R.string.advanced_settings))
        screen.addPreference(advancedSettingsCat)
        advancedSettingsCat.addPreference(buildShowUpdatedSwitchPref(context))
//        advancedSettingsCat.addPreference(buildFullWidthSwitchPref(context))

        preferenceScreen = screen
    }

    private fun buildPrefCat(
        context: Context, prefKey: String, prefTitle: String
    ): PreferenceCategory {
        return PreferenceCategory(context).apply {
            key = prefKey
            title = prefTitle
        }
    }

    private fun buildDataPlanEditTextPref(context: Context): EditTextPreference {
        return EditTextPreference(context).apply {
            key = PREF_DATAPLAN_KEY
            title = context.getString(R.string.monthly_data)
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
            key = PREF_DATAPLAN_UNIT_KEY
            title = context.getString(R.string.data_unit)
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_list_alt_24)
            entryValues = context.resources.getStringArray(R.array.data_plan_sizes)
            entries = context.resources.getStringArray(R.array.data_plan_sizes)
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            setDefaultValue("GB")
        }
    }

    private fun buildFontSizeSeekBarPref(context: Context): SeekBarPreference {
        return SeekBarPreference(context).apply {
            key = PREF_FONT_SIZE_KEY
            title = context.getString(R.string.font_size)
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_format_size_24)
            min = 4
            showSeekBarValue = true
            max = 20
            setDefaultValue(8)
        }
    }

    private fun buildTextColorPref(context: Context): ColorPickerPreference {
        return ColorPickerPreference(context).apply {
            key = PREF_TEXT_COLOR_KEY + "_" + appWidgetId.toString()
            title = context.getString(R.string.text_color)
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_color_lens_24)
            attachAlphaSlideBar = true
            attachBrightnessSlideBar = true
            positive = context.getString(R.string.confirm)
            negative = context.getString(R.string.cancel)
            cornerRadius = 26
            defaultColor = ContextCompat.getColor(context, R.color.widgetDefaultTextColor)
            onInit()
        }
    }

    private fun buildBackgroundColorPref(context: Context): ColorPickerPreference {
        return ColorPickerPreference(context).apply {
            key = PREF_BACKGROUND_COLOR_KEY + "_" + appWidgetId.toString()
            title = context.getString(R.string.background_color)
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_color_lens_24)
            attachAlphaSlideBar = true
            attachBrightnessSlideBar = true
            positive = context.getString(R.string.confirm)
            negative = context.getString(R.string.cancel)
            cornerRadius = 26
            defaultColor = ContextCompat.getColor(context, R.color.widgetDefaultBackgroundColor)
            onInit()
        }
    }

    private fun buildShowUpdatedSwitchPref(context: Context): SwitchPreferenceCompat {
        return SwitchPreferenceCompat(context).apply {
            key = PREF_SHOW_UPDATED_AT_KEY
            title = context.getString(R.string.show_updated_at)
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_access_time_24)
            summary = context.getString(R.string.show_updated_at_summary)
        }
    }

//    private fun buildFullWidthSwitchPref(context: Context): SwitchPreferenceCompat {
//        return SwitchPreferenceCompat(context).apply {
//            key = PREF_FULL_WIDTH_KEY
//            title = context.getString(R.string.full_width)
//            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_view_carousel_24)
//            summary = "By default the widget will only use as much space as needed."
//            isEnabled = false
//        }
//    }
}
