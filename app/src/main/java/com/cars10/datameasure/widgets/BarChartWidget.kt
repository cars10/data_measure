package com.cars10.datameasure.widgets

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
import com.cars10.datameasure.*
import java.text.DateFormat
import java.util.*
import kotlin.math.ceil

private var MANUAL_WIDGET_UPDATE = "manualWidgetUpdate"

class BarChartWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            context.deleteSharedPreferences(widgetPrefsName(appWidgetId))
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle
    ) {
        val updateViews = RemoteViews(context.packageName, R.layout.bar_chart_widget)
        appWidgetManager.updateAppWidget(appWidgetId, updateViews)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == MANUAL_WIDGET_UPDATE) {
            val appWidgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)

            if (appWidgetIds != null && context != null) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                for (appWidgetId in appWidgetIds) {
                    Toast.makeText(
                        context, context.getString(R.string.reload_data_usage), Toast.LENGTH_SHORT
                    ).show()
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }
    }
}

internal fun updateAppWidget(
    context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int
) {
    val widgetPrefs = DataUsageWidgetPrefs(context, appWidgetId)
    val views = RemoteViews(context.packageName, R.layout.bar_chart_widget)

    val dataPlanBytes = widgetPrefs.dataPlanBytes()
    val totalUsageBytes = NetworkUsage().summaryCurrentMonth(context)

    val perc = (totalUsageBytes.toFloat() / dataPlanBytes.toFloat()) * 100
    if (widgetPrefs.showPercentage()) {
        val str = context.getString(R.string.data_usage_percentage, ceil(perc).toInt().toString())
        views.setTextViewText(R.id.data_usage_text, str)
    } else {
        views.setTextViewText(
            R.id.data_usage_text, ByteFormatter().humanReadableByteCountBin(totalUsageBytes)
        )
    }
    views.setTextViewTextSize(
        R.id.data_usage_text, TypedValue.COMPLEX_UNIT_SP, widgetPrefs.fontSize().toFloat() * 2
    )

    views.setProgressBar(R.id.progress_bar, 100, perc.toInt(), false)

    if (widgetPrefs.showUpdatedAt()) {
        views.setViewVisibility(R.id.updated_at, View.VISIBLE)
    } else {
        views.setViewVisibility(R.id.updated_at, View.GONE)
    }
    views.setInt(
        R.id.widget_inner_layout, "setBackgroundColor", widgetPrefs.backgroundColor()
    )
    views.setTextColor(R.id.data_usage_text, widgetPrefs.textColor())

    val timeString = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date())
    views.setTextViewText(R.id.updated_at, timeString)

//    views.setOnClickPendingIntent(
//        R.id.widget_inner_layout, manualWidgetUpdateIntent(context, appWidgetId)
//    )


    val intent = Intent(context, WidgetDetails::class.java)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

    val pendingIntent =
        PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    views.setOnClickPendingIntent(R.id.widget_inner_layout, pendingIntent)




    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun manualWidgetUpdateIntent(context: Context, appWidgetId: Int): PendingIntent {
    val intentUpdate = Intent(context, BarChartWidget::class.java)
    intentUpdate.action = MANUAL_WIDGET_UPDATE
    val idArray = intArrayOf(appWidgetId)
    intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray)
    return PendingIntent.getBroadcast(
        context,
        appWidgetId,
        intentUpdate,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
}