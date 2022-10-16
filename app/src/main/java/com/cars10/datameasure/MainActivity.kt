package com.cars10.datameasure

import android.app.AppOpsManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cars10.datameasure.widgets.BarChartWidget


class WidgetsAdapter(private val context: Context, private val widgetIds: IntArray) :
    RecyclerView.Adapter<WidgetsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val widgetPreview: FragmentContainerView = itemView.findViewById(R.id.widget_preview)
        val card: CardView = itemView.findViewById(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val widgetView = inflater.inflate(R.layout.widget_row, parent, false)
        return ViewHolder(widgetView)
    }

    override fun onBindViewHolder(viewHolder: WidgetsAdapter.ViewHolder, position: Int) {
        val widgetId = widgetIds[position]

        val newContainerId = View.generateViewId()
        viewHolder.widgetPreview.id = newContainerId

        val widgetPreview = BarChartWidgetFragment().apply {
            appWidgetId = widgetId
        }
        val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
        manager.beginTransaction().replace(newContainerId, widgetPreview).commit()

        viewHolder.card.setOnClickListener { view ->
            val intent = Intent(context, BarChartWidgetConfigureActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

            view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return widgetIds.size
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.check_permission_button)
        val granted = getUsageAccessPermission()
        if (granted) {
            button.visibility = View.GONE
        } else {
            button.visibility = View.VISIBLE
        }
        setStatusText(granted)

        button.setOnClickListener {
            askUsageAccessPermission()
        }
    }

    override fun onResume() {
        super.onResume()

        val button = findViewById<Button>(R.id.check_permission_button)
        val granted = getUsageAccessPermission()
        if (granted) {
            button.visibility = View.GONE
        } else {
            button.visibility = View.VISIBLE
        }
        setStatusText(granted)

        val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(
            ComponentName(
                this, BarChartWidget::class.java
            )
        )
        ids.sort()
        val rvWidgets = findViewById<View>(R.id.widget_list) as RecyclerView
        val adapter = WidgetsAdapter(this@MainActivity, ids)
        rvWidgets.adapter = adapter
        rvWidgets.layoutManager = LinearLayoutManager(this)

        if (ids.isNotEmpty()) {
            val configureText = findViewById<TextView>(R.id.configure_text)
            configureText.visibility = View.VISIBLE
        }
    }

    private fun setStatusText(granted: Boolean) {
        val setupText = findViewById<TextView>(R.id.setup_text)
        val statusText = findViewById<TextView>(R.id.permission_status_text)
        if (granted) {
            statusText.text = getString(R.string.permissions_granted)
            setupText.text = getString(R.string.setup_done_text)
        } else {
            statusText.text = getString(R.string.permissions_not_granted)
            setupText.text = getString(R.string.setup_text)
        }
    }

    private fun askUsageAccessPermission() {
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