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
import io.realm.Realm
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.MetricsAdapter
import jp.cordea.mackerelclient.databinding.ActivityServiceMetricsBinding
import jp.cordea.mackerelclient.databinding.ContentServiceMetricsBinding
import jp.cordea.mackerelclient.model.MetricsParameter
import jp.cordea.mackerelclient.model.UserMetric
import jp.cordea.mackerelclient.viewmodel.MetricsViewModel
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class ServiceMetricsActivity : AppCompatActivity() {

    private lateinit var contentBinding: ContentServiceMetricsBinding

    private val viewModel by lazy {
        MetricsViewModel(this)
    }

    private var subscription: Subscription? = null

    private var serviceName: String? = null

    private var needRefresh = false

    private var drawCompleteMetrics = 0

    private var enableRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityServiceMetricsBinding>(
            this,
            R.layout.activity_service_metrics
        )
        contentBinding = binding.content
        lifecycle.addObserver(viewModel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        contentBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        val serviceName = intent.getStringExtra(SERVICE_NAME_KEY)

        contentBinding.swipeRefresh.setOnRefreshListener {
            if (enableRefresh) {
                refresh(serviceName)
            }
            contentBinding.swipeRefresh.isRefreshing = false
        }

        needRefresh = true
        this.serviceName = serviceName
    }

    override fun onResume() {
        super.onResume()
        if (needRefresh) {
            refresh(serviceName!!)
            needRefresh = false
        }
    }

    private fun refresh(serviceName: String) {
        enableRefresh = false
        val realm = Realm.getDefaultInstance()
        val metrics = realm.copyFromRealm(
            realm.where(UserMetric::class.java)
                .equalTo("type", MetricsType.SERVICE.name)
                .equalTo("parentId", serviceName).findAll()
        )
        realm.close()

        val item = metrics.map { MetricsParameter(it.id, null, it.label!!) }
        contentBinding.recyclerView.adapter =
            MetricsAdapter(this, item as MutableList, MetricsType.SERVICE, serviceName)
        contentBinding.recyclerView.addItemDecoration(ListItemDecoration(this))

        drawCompleteMetrics = 0
        subscription?.unsubscribe()
        subscription = viewModel
            .onChartDataAlive
            .asObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it0 ->
                val adapter = contentBinding.recyclerView.adapter as MetricsAdapter
                drawCompleteMetrics = adapter.refreshRecyclerViewItem(it0, drawCompleteMetrics)
                if (adapter.itemCount == drawCompleteMetrics) {
                    enableRefresh = true
                }
            }, {})

        contentBinding.run {
            if (metrics.size == 0) {
                noticeContainer.visibility = View.VISIBLE
                swipeRefresh.visibility = View.GONE
            } else {
                swipeRefresh.visibility = View.VISIBLE
                noticeContainer.visibility = View.GONE
                viewModel.requestMetricsApi(metrics, serviceName, MetricsType.SERVICE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.unsubscribe()
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
                    .createIntent(this, MetricsType.SERVICE, serviceName!!)
                startActivityForResult(intent, MetricsEditActivity.REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private const val SERVICE_NAME_KEY = "ServiceNameKey"

        fun createIntent(context: Context, name: String): Intent =
            Intent(context, ServiceMetricsActivity::class.java).apply {
                putExtra(ServiceMetricsActivity.SERVICE_NAME_KEY, name)
            }
    }
}
