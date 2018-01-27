package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.databinding.ActivityDetailCommonBinding
import jp.cordea.mackerelclient.fragment.MonitorSettingDeleteDialogFragment
import jp.cordea.mackerelclient.viewmodel.MonitorDetailViewModel
import rx.Subscription

class MonitorDetailActivity : AppCompatActivity() {

    var monitor: Monitor? = null

    private var subscription: Subscription? = null

    private val viewModel by lazy {
        MonitorDetailViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
                .setContentView<ActivityDetailCommonBinding>(this, R.layout.activity_detail_common)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val monitor = intent.getSerializableExtra(MONITOR_KEY) as Monitor

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = DetailCommonAdapter(this, viewModel.getDisplayData(monitor))
        binding.recyclerView.addItemDecoration(ListItemDecoration(this))
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

        const val REQUEST_CODE = 0

        private const val MONITOR_KEY = "MONITOR_KEY"

        fun createIntent(context: Context, monitor: Monitor): Intent =
                Intent(context, MonitorDetailActivity::class.java).apply {
                    putExtra(MONITOR_KEY, monitor)
                }
    }
}
