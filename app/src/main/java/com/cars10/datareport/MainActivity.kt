package com.cars10.datareport

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setStatusText(getUsageAccessPermission())

        if (getUsageAccessPermission()) {
            val result = findViewById<TextView>(R.id.result)
            Thread {
                val totalUsage = NetworkUsage().summary(
                    applicationContext, 0, System.currentTimeMillis()
                )

                result.text = ByteFormatter().humanReadableByteCountBin(totalUsage)
            }.start()
        }
    }

    override fun onResume() {
        super.onResume()

        setStatusText(getUsageAccessPermission())
    }

    fun checkPermissionStatus(view: View) {
        askUsageAccessPermission(view)
    }

    private fun setStatusText(granted: Boolean) {
        val statusText = findViewById<TextView>(R.id.statusText)
        if (granted) {
            statusText.text = getString(R.string.permissions_granted)
            statusText.setTextColor(Color.GREEN)
        } else {
            statusText.text = getString(R.string.permissions_not_granted)
        }
    }

    private fun askUsageAccessPermission(_view: View) {
        if (!getUsageAccessPermission()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }
    }

    private fun getUsageAccessPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(), packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
}