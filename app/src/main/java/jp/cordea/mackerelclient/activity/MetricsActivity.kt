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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.realm.Realm
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.MetricsAdapter
import jp.cordea.mackerelclient.databinding.ActivityMetricsBinding
import jp.cordea.mackerelclient.databinding.ContentMetricsBinding
import jp.cordea.mackerelclient.model.MetricsParameter
import jp.cordea.mackerelclient.model.UserMetric
import jp.cordea.mackerelclient.viewmodel.MetricsViewModel

class MetricsActivity : AppCompatActivity() {

    private val viewModel by lazy { MetricsViewModel(this) }

    private val disposable = SerialDisposable()

    private var hostId: String? = null
    private var needRefresh = false
    private var drawCompleteMetrics = 0
    private var enableRefresh = false

    private lateinit var contentBinding: ContentMetricsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityMetricsBinding>(this, R.layout.activity_metrics)
        contentBinding = binding.content
        lifecycle.addObserver(viewModel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        contentBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        val hostId = intent.getStringExtra(HOST_ID_KEY)

        contentBinding.swipeRefresh.setOnRefreshListener {
            if (enableRefresh) {
                refresh(hostId)
            }
            contentBinding.swipeRefresh.isRefreshing = false
        }

        needRefresh = true
        this.hostId = hostId
    }

    override fun onResume() {
        super.onResume()
        if (needRefresh) {
            refresh(hostId!!)
            needRefresh = false
        }
    }

    private fun refresh(hostId: String) {
        enableRefresh = false
        val realm = Realm.getDefaultInstance()
        val metrics = realm.copyFromRealm(
            realm.where(UserMetric::class.java)
                .equalTo("type", MetricsType.HOST.name)
                .equalTo("parentId", hostId).findAll()
        )
        val item = metrics.map { MetricsParameter(it.id, null, it.label!!) }
        realm.close()

        contentBinding.recyclerView.adapter =
            MetricsAdapter(this, item as MutableList, MetricsType.HOST, hostId)
        contentBinding.recyclerView.addItemDecoration(ListItemDecoration(this))

        drawCompleteMetrics = 0
        viewModel
            .onChartDataAlive
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val adapter = contentBinding.recyclerView.adapter as MetricsAdapter
                drawCompleteMetrics = adapter.refreshRecyclerViewItem(it, drawCompleteMetrics)
                if (adapter.itemCount == drawCompleteMetrics) {
                    enableRefresh = true
                }
            }, {})
            .run(disposable::set)

        if (metrics.size == 0) {
            contentBinding.noticeContainer.visibility = View.VISIBLE
            contentBinding.templateButton.setOnClickListener {
                contentBinding.templateButton.isClickable = false
                viewModel.initUserMetrics(hostId)
                refresh(hostId)
            }
            contentBinding.swipeRefresh.visibility = View.GONE
        } else {
            contentBinding.swipeRefresh.visibility = View.VISIBLE
            contentBinding.noticeContainer.visibility = View.GONE
            viewModel.requestMetricsApi(metrics, hostId, MetricsType.HOST)
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
                val intent = MetricsEditActivity.createIntent(this, MetricsType.HOST, hostId!!)
                startActivityForResult(intent, MetricsEditActivity.REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val HOST_ID_KEY = "HostIdKey"

        fun createIntent(context: Context, hostId: String?): Intent =
            Intent(context, MetricsActivity::class.java).apply {
                putExtra(HOST_ID_KEY, hostId)
            }
    }
}
