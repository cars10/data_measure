package com.cars10.datameasure

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.DateFormat
import java.util.*


class BarChartWidgetFragment : Fragment(R.layout.bar_chart_widget) {
    var appWidgetId: Int = -1
    private var listener: OnSharedPreferenceChangeListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val view = requireView()
        val preferences = activity?.getSharedPreferences(widgetPrefsName(appWidgetId), 0)
        updatePreview(view)
        listener = OnSharedPreferenceChangeListener { prefs, key ->
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

        val totalUsage = NetworkUsage().summaryCurrentMonth(activity)
        val str = ByteFormatter().humanReadableByteCountBin(totalUsage)
        val txt = view.findViewById<TextView>(R.id.appwidget_text)

        txt.text = str
        txt.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            widgetPrefs.fontSize().toFloat() * 2
        )

        val dataPlan = widgetPrefs.dataPlan().toInt()
        val dataPlanUnit = widgetPrefs.dataPlanUnit()
        var progressValue = dataPlan
        if (dataPlanUnit == "GB") progressValue *= 1000
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = (totalUsage / 1000 / 1000).toInt()
        progressBar.max = progressValue

        val reloadButton = view.findViewById<ImageView>(R.id.reload_button)
        if (widgetPrefs.showReload()) {
            reloadButton.visibility = View.VISIBLE
        } else {
            reloadButton.visibility = View.GONE
        }

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
        txt.setTextColor(widgetPrefs.textColor())
    }
}