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
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.MonitorDetailActivity
import jp.cordea.mackerelclient.adapter.MonitorAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Monitor
import kotterknife.bindView
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.Subscriptions

class MonitorFragment : android.support.v4.app.Fragment() {

    val progress: View by bindView(R.id.progress)

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    val swipeRefresh: SwipeRefreshLayout by bindView(R.id.swipe_refresh)

    val error: View by bindView(R.id.error)

    private var subscription: Subscription? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_monitor, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        refresh()

        swipeRefresh.setOnRefreshListener {
            refresh()
        }

        val retry: Button = error.findViewById(R.id.retry_button) as Button
        retry.setOnClickListener {
            progress.visibility = View.VISIBLE
            error.visibility = View.GONE
            refresh()
        }
    }

    private fun refresh() {
        swipeRefresh.isRefreshing = true
        subscription?.unsubscribe()
        subscription = requestApi()
    }

    private fun requestApi(): Subscription {
        val context = context ?: return Subscriptions.empty()
        return MackerelApiClient
                .getMonitors(context)
                .map {
                    val sections = it.monitors.map { it.type }.distinct().sortedBy { it }
                    val monitors: MutableList<Pair<String, Monitor?>> = arrayListOf()
                    for (section in sections) {
                        val items = it.monitors.filter { section == it.type }
                        val type = items[0].type
                        monitors.add(Pair(type, null))
                        items.map { monitors.add(Pair(type, it)) }
                    }
                    monitors
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    swipeRefresh.isRefreshing = false
                    recyclerView.adapter = MonitorAdapter(this, it)
                    progress.visibility = View.GONE
                    swipeRefresh.visibility = View.VISIBLE
                }, {
                    swipeRefresh.isRefreshing = false
                    error.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MonitorDetailActivity.RequestCode) {
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
        fun newInstance(): MonitorFragment =
                MonitorFragment()
    }
}
