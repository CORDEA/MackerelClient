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
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.HostDetailActivity
import jp.cordea.mackerelclient.adapter.HostAdapter
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.viewmodel.HostViewModel
import kotterknife.bindView
import rx.Subscription

/**
 * Created by Yoshihiro Tanaka on 16/01/12.
 */
class HostFragment : android.support.v4.app.Fragment() {

    val progress: View by bindView(R.id.progress)

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    val swipeRefresh: SwipeRefreshLayout by bindView(R.id.swipe_refresh)

    val error: View by bindView(R.id.error)

    private var subscription: Subscription? = null

    private val viewModel by lazy {
        HostViewModel(context)
    }

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
        subscription?.unsubscribe()
        subscription = getHosts(viewModel.displayHostState)
    }

    private fun getHosts(items: List<DisplayHostState>): Subscription {
        return viewModel
                .getHosts(items)
                .subscribe({
                    viewModel
                            .getLatestMetrics(it)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HostDetailActivity.RequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                refresh()
            }
        }
    }

    override fun onDestroyView() {
        subscription?.unsubscribe()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): HostFragment {
            val fragment = HostFragment()
            return fragment
        }
    }
}