package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import butterknife.bindView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.utils.DialogUtils
import retrofit2.Callback
import retrofit2.Response
import rx.Subscription

class MonitorDetailActivity : AppCompatActivity() {
    companion object {
        public val MonitorKey = "MonitorKey"
        public val RequestCode = 0
    }

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    var monitor: Monitor? = null

    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_common)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val monitor = intent.getParcelableExtra<Monitor>(MonitorKey)

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = DetailCommonAdapter(applicationContext, createData(monitor))

        this.monitor = monitor
    }

    private fun createData(monitor: Monitor) : List<List<Pair<String, Int>>> {
        val list: MutableList<MutableList<Pair<String, Int>>> = arrayListOf()
        var inner: MutableList<Pair<String, Int>> = arrayListOf()
        monitor.name?.let {
            inner.add(Pair(it, R.string.monitor_detail_name))
        }
        monitor.service?.let {
            inner.add(Pair(it, R.string.monitor_detail_service))
        }
        inner.add(Pair(monitor.type!!, R.string.monitor_detail_type))

        monitor.duration?.let {
            inner.add(Pair(it.toString(), R.string.monitor_detail_duration))
        }
        monitor.notificationInterval?.let {
            inner.add(Pair(it.toString(), R.string.monitor_detail_not_interval))
        }
        list.add(inner)

        inner = arrayListOf()
        monitor.metric?.let {
            inner.add(Pair(it, R.string.monitor_detail_metric))
        }
        monitor.operator?.let { op ->
            monitor.critical?.let {
                inner.add(Pair("%s %s".format(op, it.toString()), R.string.monitor_detail_critical))
            }
            monitor.warning?.let {
                inner.add(Pair("%s %s".format(op, it.toString()), R.string.monitor_detail_warning))
            }
        }
        list.add(inner)

        inner = arrayListOf()
        if (monitor.scopes.size > 0) {
            inner.add(Pair(monitor.scopes.joinToString(", "), R.string.monitor_detail_scope))
        }
        if (monitor.excludeScopes.size > 0) {
            inner.add(Pair(monitor.excludeScopes.joinToString(", "), R.string.monitor_detail_ex_scope))
        }
        list.add(inner)

        inner = arrayListOf()
        monitor.url?.let {
            inner.add(Pair(it, R.string.monitor_detail_url))
        }
        list.add(inner)
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.monitor_detail, menu)
        monitor?.let {
            if ("connectivity".equals(it.type)) {
                menu.findItem(R.id.action_delete).setVisible(false)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPause() {
        super.onPause()
        subscription?.let {
            it.unsubscribe()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val context = this
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_delete -> {
                AlertDialog
                        .Builder(context)
                        .setMessage(R.string.monitor_detail_delete_dialog_title)
                        .setPositiveButton(R.string.delete_positive_button, { dialogInterface, i ->
                            val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                            dialog.show()
                            MackerelApiClient
                                    .deleteMonitor(context, monitor!!.id!!)
                                    .enqueue(object : Callback<Monitor> {
                                        override fun onResponse(response: Response<Monitor>?) {
                                            dialog.dismiss()
                                            response?.let {
                                                val success = DialogUtils.switchDialog(context, it,
                                                        R.string.monitor_detail_error_dialog_title,
                                                        R.string.error_403_dialog_message)
                                                if (success) {
                                                    setResult(Activity.RESULT_OK)
                                                    finish()
                                                }
                                                return
                                            }
                                            DialogUtils.showDialog(context,
                                                    R.string.monitor_detail_error_dialog_title)
                                        }

                                        override fun onFailure(t: Throwable?) {
                                            dialog.dismiss()
                                            DialogUtils.showDialog(context,
                                                    R.string.monitor_detail_error_dialog_title)
                                        }
                                    })
                        }).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
