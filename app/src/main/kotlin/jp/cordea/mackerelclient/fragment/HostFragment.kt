package jp.cordea.mackerelclient.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import butterknife.bindView
import io.realm.Realm
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.HostDetailActivity
import jp.cordea.mackerelclient.adapter.HostAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.model.UserMetric
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by Yoshihiro Tanaka on 16/01/12.
 */
class HostFragment : android.support.v4.app.Fragment() {

    val progress: View by bindView(R.id.progress)

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    val swipeRefresh: SwipeRefreshLayout by bindView(R.id.swipe_refresh)

    val error: View by bindView(R.id.error)

    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_host, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        refresh()

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
        swipeRefresh.isRefreshing = true
        val realm = Realm.getDefaultInstance()
        initDisplayHostState(realm)
        var items = realm.copyFromRealm(realm.where(DisplayHostState::class.java).findAll())
        realm.close()
        items = items.filter { it.isDisplay!! }

        subscription?.unsubscribe()
        subscription = requestApi(items)
    }

    private fun initDisplayHostState(realm: Realm) {
        if (realm.where(DisplayHostState::class.java).findAll().size == 0) {
            realm.executeTransaction {
                for (key in resources.getStringArray(R.array.setting_host_cell_arr)) {
                    val item = it.createObject(DisplayHostState::class.java)
                    item.name = key
                    item.isDisplay = (key == "standby" || key == "working")
                }
            }
        }
    }

    private fun requestApi(items: List<DisplayHostState>): Subscription {
        return MackerelApiClient
                .getHosts(context, items.map { it.name })
                .filter {
                    deleteOldMetricData(it.hosts.map { it.id!! })
                    true
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    MackerelApiClient
                            .getLatestMetrics(context, it.hosts.map { it.id!! }, arrayListOf("loadavg5", "cpu.user.percentage", "memory.used"))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ it2 ->
                                recyclerView.adapter = HostAdapter(this, it.hosts, it2.tsdbs)
                                recyclerView.addItemDecoration(ListItemDecoration(context))
                                progress.visibility = View.GONE
                                swipeRefresh.visibility = View.VISIBLE
                                swipeRefresh.isRefreshing = false
                            })
                }, {
                    it.printStackTrace()
                    swipeRefresh.isRefreshing = false
                    error.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                    swipeRefresh.visibility = View.GONE
                })
    }

    private fun deleteOldMetricData(hosts: List<String>) {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(UserMetric::class.java)
                        .equalTo("type", MetricsType.HOST.name).findAll()
        val olds = results.map { it.parentId }.distinct().filter { !hosts.contains(it) }
        realm.executeTransaction {
            for (old in olds) {
                realm.where(UserMetric::class.java)
                        .equalTo("parentId", old)
                        .findAll()
                        .deleteAllFromRealm()
            }
        }
        realm.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HostDetailActivity.RequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                refresh()
            }
        }
    }

    override fun onDestroyView() {
        subscription?.let(Subscription::unsubscribe)
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): HostFragment {
            val fragment = HostFragment()
            return fragment
        }
    }
}