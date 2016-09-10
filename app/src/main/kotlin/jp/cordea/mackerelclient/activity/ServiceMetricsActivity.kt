package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.bindView
import io.realm.Realm
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.MetricsViewModel
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.MetricsAdapter
import jp.cordea.mackerelclient.model.MetricsParameter
import jp.cordea.mackerelclient.model.UserMetric
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class ServiceMetricsActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val noticeView: View by bindView(R.id.notice_container)

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    val swipeRefresh: SwipeRefreshLayout by bindView(R.id.swipe_refresh)

    private var viewModel: MetricsViewModel? = null

    private var subscription: Subscription? = null

    private var serviceName: String? = null

    private var needRefresh = false

    private var drawCompleteMetrics = 0

    private var enableRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_metrics)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(this)
        var serviceName = intent.getStringExtra(ServiceNameKey)

        swipeRefresh.setOnRefreshListener {
            if (enableRefresh) {
                refresh(serviceName)
            }
            swipeRefresh.isRefreshing = false
        }

        viewModel = MetricsViewModel(this)

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
                        .equalTo("parentId", serviceName).findAll())
        realm.close()

        val item = metrics.map { MetricsParameter(it.id, null, it.label!!) }
        recyclerView.adapter = MetricsAdapter(this, item as MutableList, MetricsType.SERVICE, serviceName)

        drawCompleteMetrics = 0
        subscription?.let {
            it.unsubscribe()
        }
        subscription = viewModel!!
                .onChartDataAlive
                .asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it0 ->
                    val adapter = recyclerView.adapter as MetricsAdapter
                    drawCompleteMetrics = adapter.refreshRecyclerViewItem(it0, drawCompleteMetrics)
                    if (adapter.itemCount == drawCompleteMetrics) {
                        enableRefresh = true
                    }
                }, {})

        if (metrics.size == 0) {
            noticeView.visibility = View.VISIBLE
            swipeRefresh.visibility = View.GONE
        } else {
            swipeRefresh.visibility = View.VISIBLE
            noticeView.visibility = View.GONE
            viewModel!!.requestMetricsApi(metrics, serviceName, MetricsType.SERVICE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel!!.subscription?.let {
            it.unsubscribe()
        }
        subscription?.let {
            it.unsubscribe()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.metric, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MetricsEditActivity.RequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                needRefresh = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_add -> {
                val intent = MetricsEditActivity.newInstance(applicationContext, MetricsType.SERVICE, serviceName!!)
                startActivityForResult(intent, MetricsEditActivity.RequestCode)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        public val ServiceNameKey = "ServiceNameKey"
    }

}
