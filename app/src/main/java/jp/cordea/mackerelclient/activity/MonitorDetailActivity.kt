package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.api.response.MonitorDataResponse
import jp.cordea.mackerelclient.databinding.ActivityDetailCommonBinding
import jp.cordea.mackerelclient.fragment.MonitorSettingDeleteDialogFragment
import jp.cordea.mackerelclient.viewmodel.MonitorDetailViewModel
import javax.inject.Inject

class MonitorDetailActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private val viewModel by lazy { MonitorDetailViewModel() }

    private var monitor: MonitorDataResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityDetailCommonBinding>(this, R.layout.activity_detail_common)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val monitor = intent.getSerializableExtra(MONITOR_KEY) as MonitorDataResponse

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

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    companion object {
        const val REQUEST_CODE = 0

        private const val MONITOR_KEY = "MONITOR_KEY"

        fun createIntent(context: Context, monitor: MonitorDataResponse): Intent =
            Intent(context, MonitorDetailActivity::class.java).apply {
                putExtra(MONITOR_KEY, monitor)
            }
    }
}
