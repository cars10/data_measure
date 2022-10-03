package com.cars10.datameasure

import android.app.AppOpsManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cars10.datameasure.widgets.BarChartWidget

class Widget(val id: Int) {
    companion object {
        fun createWidgetsList(ids: IntArray): ArrayList<Widget> {
            val widgets = ArrayList<Widget>()
            for (id in ids) {
                widgets.add(Widget(id))
            }
            return widgets
        }
    }
}

class WidgetsAdapter(private val context: Context, private val mWidgets: List<Widget>) :
    RecyclerView.Adapter<WidgetsAdapter.ViewHolder>() {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val widgetPreview = itemView.findViewById<FragmentContainerView>(R.id.widget_preview)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val widgetView = inflater.inflate(R.layout.widget_row, parent, false)
        // Return a new holder instance
        return ViewHolder(widgetView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: WidgetsAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val widget: Widget = mWidgets.get(position)

        val newContainerId = View.generateViewId() // Generate unique container id
        viewHolder.widgetPreview.id = newContainerId;// Set container id

        val widgetPreview = BarChartWidgetFragment().apply {
            appWidgetId = widget.id
        }
        val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
        manager.beginTransaction().replace(newContainerId, widgetPreview).commit()
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mWidgets.size
    }
}

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

        val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(
            ComponentName(
                this, BarChartWidget::class.java
            )
        )
        ids.sort()
        val rvWidgets = findViewById<View>(R.id.widget_list) as RecyclerView
        val widgets = Widget.createWidgetsList(ids)
        val adapter = WidgetsAdapter(this@MainActivity, widgets)
        rvWidgets.adapter = adapter
        rvWidgets.layoutManager = LinearLayoutManager(this)
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
                AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName
            )
        } else {
            @Suppress("DEPRECATION") appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
}