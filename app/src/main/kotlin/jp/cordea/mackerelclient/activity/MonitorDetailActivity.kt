package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.fragment.MonitorSettingDeleteDialogFragment
import jp.cordea.mackerelclient.viewmodel.MonitorDetailViewModel
import kotterknife.bindView
import rx.Subscription

class MonitorDetailActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    var monitor: Monitor? = null

    private var subscription: Subscription? = null

    private val viewModel by lazy {
        MonitorDetailViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_common)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val monitor = intent.getSerializableExtra(MonitorKey) as Monitor

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DetailCommonAdapter(this, viewModel.getDisplayData(monitor))
        recyclerView.addItemDecoration(ListItemDecoration(this))
        this.monitor = monitor
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

    companion object {

        public val RequestCode = 0

        private val MonitorKey = "MonitorKey"

        fun createIntent(context: Context, monitor: Monitor): Intent {
            return Intent(context, MonitorDetailActivity::class.java).apply {
                putExtra(MonitorKey, monitor)
            }
        }
    }
}
