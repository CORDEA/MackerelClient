package jp.cordea.mackerelclient.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import butterknife.bindView
import io.realm.Realm
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.ServiceMetricsActivity
import jp.cordea.mackerelclient.adapter.ServiceAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Service
import jp.cordea.mackerelclient.model.UserMetric
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class ServiceFragment : android.support.v4.app.Fragment() {

    val listView: ListView by bindView(R.id.list)

    val swipeRefresh: SwipeRefreshLayout by bindView(R.id.swipe_refresh)

    val progress: View by bindView(R.id.progress)

    val error: View by bindView(R.id.error)

    private var services: List<Service>? = null

    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_service, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        refresh()

        listView.setOnItemClickListener { adapterView, view, i, l ->
            services?.let {
                val intent = Intent(context, ServiceMetricsActivity::class.java)
                intent.putExtra(ServiceMetricsActivity.ServiceNameKey, it[i].name)
                startActivity(intent)
            }
        }

        swipeRefresh.setOnRefreshListener {
            refresh()
        }

        val retry: Button = error.findViewById(R.id.retry) as Button
        retry.setOnClickListener {
            progress.visibility = View.VISIBLE
            error.visibility = View.GONE
            refresh()
        }
    }

    private fun refresh() {
        subscription?.let {
            it.unsubscribe()
        }
        subscription = requestApi()
    }

    private fun requestApi(): Subscription {
        return MackerelApiClient
                .getServices(context)
                .filter {
                    deleteOldMetricData(it.services.map { it.name })
                    true
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    swipeRefresh.isRefreshing = false
                    listView.adapter = ServiceAdapter(context, it.services)
                    swipeRefresh.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                    services = it.services
                }, {
                    it.printStackTrace()
                    swipeRefresh.isRefreshing = false
                    error.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                })
    }

    private fun deleteOldMetricData(hosts: List<String>) {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(UserMetric::class.java)
                        .equalTo("type", MetricsType.SERVICE.name).findAll()
        realm.executeTransaction {
            val olds = results.map { it.parentId }.distinct().filter { !hosts.contains(it) }
            for (old in olds) {
                realm.where(UserMetric::class.java)
                        .equalTo("parentId", old)
                        .findAll()
                        .deleteAllFromRealm()
            }
        }
        realm.close()
    }

    override fun onDestroyView() {
        subscription?.let {
            it.unsubscribe()
        }
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): ServiceFragment {
            val fragment = ServiceFragment()
            return fragment
        }
    }
}
