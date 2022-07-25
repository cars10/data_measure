package com.cars10.datameasure

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class NetworkUsage {
    fun summary(context: Context, startTime: Long, untilTime: Long): Long {
        val networkStatsManager =
            context.getSystemService(AppCompatActivity.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val bucket: NetworkStats.Bucket = networkStatsManager.querySummaryForDevice(
            NetworkCapabilities.TRANSPORT_CELLULAR,
            null,
            startTime,
            untilTime
        )
        return bucket.txBytes + bucket.rxBytes
    }

    fun summaryCurrentMonth(context: Context): Long {
        return summary(context, firstDayOfCurrentMonthMillis(), System.currentTimeMillis())
    }
}

fun firstDayOfCurrentMonthMillis(): Long {
    val cal = Calendar.getInstance()
    cal[Calendar.HOUR_OF_DAY] = 0 // ! clear would not reset the hour of day !
    cal.clear(Calendar.MINUTE)
    cal.clear(Calendar.SECOND)
    cal.clear(Calendar.MILLISECOND)
    cal[Calendar.DAY_OF_MONTH] = 1

    return cal.timeInMillis
}