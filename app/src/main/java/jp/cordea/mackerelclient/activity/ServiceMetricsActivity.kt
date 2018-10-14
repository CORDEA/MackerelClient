package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import io.reactivex.disposables.SerialDisposable
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.MetricsAdapter
import jp.cordea.mackerelclient.databinding.ActivityServiceMetricsBinding
import jp.cordea.mackerelclient.databinding.ContentServiceMetricsBinding
import jp.cordea.mackerelclient.fragment.MetricsDeleteConfirmDialogFragment
import jp.cordea.mackerelclient.viewmodel.ServiceMetricsViewModel
import javax.inject.Inject

class ServiceMetricsActivity : AppCompatActivity(),
    MetricsDeleteConfirmDialogFragment.OnDeleteMetricsListener {

    @Inject
    lateinit var viewModel: ServiceMetricsViewModel

    private val disposable = SerialDisposable()

    private var needRefresh = false
    private var enableRefresh = false

    private lateinit var serviceName: String
    private lateinit var adapter: MetricsAdapter
    private lateinit var contentBinding: ContentServiceMetricsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityServiceMetricsBinding>(
            this,
            R.layout.activity_service_metrics
        )
        contentBinding = binding.content
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        serviceName = intent.getStringExtra(SERVICE_NAME_KEY)
        viewModel.start(serviceName)

        adapter = MetricsAdapter(this, MetricsType.SERVICE, serviceName)
        contentBinding.recyclerView.adapter = adapter
        contentBinding.recyclerView.addItemDecoration(ListItemDecoration(this))
        contentBinding.recyclerView.layoutManager = LinearLayoutManager(this)

        contentBinding.swipeRefresh.setOnRefreshListener {
            if (enableRefresh) {
                refresh()
            }
            contentBinding.swipeRefresh.isRefreshing = false
        }

        needRefresh = true
    }

    override fun onResume() {
        super.onResume()
        if (needRefresh) {
            refresh()
            needRefresh = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.metric, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MetricsEditActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                needRefresh = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_add -> {
                val intent = MetricsEditActivity
                    .createIntent(this, MetricsType.SERVICE, serviceName)
                startActivityForResult(intent, MetricsEditActivity.REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDelete(id: Int) {
        adapter.removeAt(id)
    }

    private fun refresh() {
        enableRefresh = false
        viewModel.fetchMetrics()
            .subscribe({
                adapter.add(it)
                contentBinding.run {
                    if (adapter.itemCount == 0) {
                        noticeContainer.visibility = View.VISIBLE
                        swipeRefresh.visibility = View.GONE
                    } else {
                        swipeRefresh.visibility = View.VISIBLE
                        noticeContainer.visibility = View.GONE
                    }
                }
            }, {
            }, {
                enableRefresh = true
            })
            .run(disposable::set)
    }

    companion object {
        private const val SERVICE_NAME_KEY = "ServiceNameKey"

        fun createIntent(context: Context, name: String): Intent =
            Intent(context, ServiceMetricsActivity::class.java).apply {
                putExtra(ServiceMetricsActivity.SERVICE_NAME_KEY, name)
            }
    }
}
