package com.cars10.datareport

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import java.text.DateFormat
import java.util.*


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [BarChartWidgetConfigureActivity]
 */
class BarChartWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        val updateViews = RemoteViews(context.packageName, R.layout.bar_chart_widget)
        val msg = String.format(
            Locale.getDefault(),
            "[%d-%d] x [%d-%d]",
            newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH),
            newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH),
            newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT),
            newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
        )

//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

        appWidgetManager.updateAppWidget(appWidgetId, updateViews)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetPrefs = DataUsageWidgetPrefs(context, appWidgetId)
    val dataPlan = widgetPrefs.dataPlan()
    val dataPlanUnit = widgetPrefs.dataPlanUnit()
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.bar_chart_widget)
    views.setTextViewText(R.id.appwidget_text, "")

    val totalUsage = NetworkUsage().summaryCurrentMonth(context)
    val str = ByteFormatter().humanReadableByteCountBin(totalUsage)
    views.setTextViewText(R.id.appwidget_text, str)
    views.setTextViewTextSize(
        R.id.appwidget_text,
        TypedValue.COMPLEX_UNIT_SP,
        widgetPrefs.fontSize().toFloat() * 6
    )

    var progressValue = dataPlan
    if (dataPlanUnit == "GB") progressValue *= 1000
    views.setProgressBar(R.id.progressBar, progressValue, (totalUsage / 1000 / 1000).toInt(), false)

    if (widgetPrefs.showReload()) {
        views.setViewVisibility(R.id.reload_button, View.VISIBLE)
        views.setOnClickPendingIntent(R.id.reload_button, triggerReload(context, appWidgetId))
    } else {
        views.setViewVisibility(R.id.reload_button, View.GONE)
    }

    if (widgetPrefs.showUpdatedAt()) {
        views.setViewVisibility(R.id.updated_at, View.VISIBLE)
    } else {
        views.setViewVisibility(R.id.updated_at, View.GONE)
    }
    views.setInt(
        R.id.widget_inner_layout, "setBackgroundColor", widgetPrefs.backgroundColor()
    )
    views.setTextColor(R.id.appwidget_text, widgetPrefs.textColor())

    val timeString = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date())
    views.setTextViewText(R.id.updated_at, timeString)

    val intent = Intent(context, BarChartWidgetConfigureActivity::class.java)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

    val pendingIntent =
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun triggerReload(context: Context, appWidgetId: Int): PendingIntent {
    val intentUpdate = Intent(context, BarChartWidget::class.java)
    intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    val idArray = intArrayOf(appWidgetId)
    intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray)
    Toast.makeText(context, "Widget has been updated! ", Toast.LENGTH_SHORT).show()
    return PendingIntent.getBroadcast(
        context, appWidgetId, intentUpdate,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}