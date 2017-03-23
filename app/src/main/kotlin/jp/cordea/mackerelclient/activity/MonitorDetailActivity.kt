package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import butterknife.bindView
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.fragment.MonitorSettingDeleteDialogFragment
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
        recyclerView.addItemDecoration(ListItemDecoration(this))

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
        if (monitor.scopes.isNotEmpty()) {
            inner.add(Pair(monitor.scopes.joinToString(", "), R.string.monitor_detail_scope))
        }
        if (monitor.excludeScopes.isNotEmpty()) {
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
            if ("connectivity" == it.type) {
                menu.findItem(R.id.action_delete).isVisible = false
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPause() {
        super.onPause()
        subscription?.unsubscribe()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_delete -> {
                MonitorSettingDeleteDialogFragment
                        .newInstance(monitor!!)
                        .apply {
                            onSuccess = {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                        .show(supportFragmentManager, "")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
