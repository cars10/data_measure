package com.cars10.datameasure

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.DateFormat
import java.util.*
import kotlin.math.ceil

class BarChartWidgetFragment : Fragment(R.layout.bar_chart_widget) {
    var appWidgetId: Int = -1
    private var listener: OnSharedPreferenceChangeListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val preferences = activity?.getSharedPreferences(widgetPrefsName(appWidgetId), 0)
        updatePreview(view)
        listener = OnSharedPreferenceChangeListener { _, _ ->
            updatePreview(view)
        }
        preferences?.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()

        val preferences = activity?.getSharedPreferences(widgetPrefsName(appWidgetId), 0)
        preferences?.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun updatePreview(view: View) {
        val activity = requireActivity()
        val widgetPrefs = DataUsageWidgetPrefs(activity, appWidgetId)

        val dataPlanBytes = widgetPrefs.dataPlanBytes()
        val totalUsageBytes = NetworkUsage().summaryCurrentMonth(activity)
        val txt = view.findViewById<TextView>(R.id.appwidget_text)
        val perc = (totalUsageBytes.toDouble() / dataPlanBytes.toDouble()) * 100

        if (widgetPrefs.showPercentage()) {
            txt.text = getString(R.string.data_usage_percentage, ceil(perc).toInt().toString())
        } else {
            txt.text = ByteFormatter().humanReadableByteCountBin(totalUsageBytes)
        }
        txt.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            widgetPrefs.fontSize().toFloat() * 2
        )

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = 100
        progressBar.progress = perc.toInt()

        val updatedAt = view.findViewById<TextView>(R.id.updated_at)
        if (widgetPrefs.showUpdatedAt()) {
            updatedAt.visibility = View.VISIBLE
        } else {
            updatedAt.visibility = View.GONE
        }

        val timeString = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date())
        updatedAt.text = timeString

        val innerLayout = view.findViewById<RelativeLayout>(R.id.widget_inner_layout)
        innerLayout.setBackgroundColor(widgetPrefs.backgroundColor())
        innerLayout.isClickable = false
        txt.setTextColor(widgetPrefs.textColor())
    }
}