package jp.cordea.mackerelclient.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import butterknife.bindView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.ServiceMetricsActivity
import jp.cordea.mackerelclient.adapter.ServiceAdapter
import jp.cordea.mackerelclient.api.response.Service
import jp.cordea.mackerelclient.viewmodel.ServiceViewModel
import rx.Subscription

class ServiceFragment : android.support.v4.app.Fragment() {

    val listView: ListView by bindView(R.id.list)

    val swipeRefresh: SwipeRefreshLayout by bindView(R.id.swipe_refresh)

    val progress: View by bindView(R.id.progress)

    val error: View by bindView(R.id.error)

    private var services: List<Service>? = null

    private var subscription: Subscription? = null

    private val viewModel by lazy {
        ServiceViewModel(context)
    }

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

        listView.setOnItemClickListener { _, _, i, _ ->
            services?.let {
                startActivity(ServiceMetricsActivity.createIntent(context, it[i].name))
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
        subscription?.let(Subscription::unsubscribe)
        subscription = getServices()
    }

    private fun getServices(): Subscription {
        return viewModel
                .getServices()
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

    override fun onDestroyView() {
        subscription?.let(Subscription::unsubscribe)
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): ServiceFragment {
            val fragment = ServiceFragment()
            return fragment
        }
    }
}
