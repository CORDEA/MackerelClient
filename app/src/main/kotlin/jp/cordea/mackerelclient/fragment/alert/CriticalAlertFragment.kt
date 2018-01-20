package jp.cordea.mackerelclient.fragment.alert

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.AlertDetailActivity
import jp.cordea.mackerelclient.adapter.AlertAdapter
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.viewmodel.AlertViewModel
import kotterknife.bindView
import rx.Subscription
import rx.subscriptions.Subscriptions

class CriticalAlertFragment : android.support.v4.app.Fragment() {

    companion object {
        const val RequestCode = 1

        fun newInstance(): CriticalAlertFragment =
                CriticalAlertFragment()
    }

    private val viewModel by lazy {
        AlertViewModel(context!!)
    }

    val listView: ListView by bindView(R.id.list)

    val swipeRefresh: SwipeRefreshLayout by bindView(R.id.swipe_refresh)

    val progress: View by bindView(R.id.progress)

    val error: View by bindView(R.id.error)

    private var alerts: List<Alert>? = null

    private var subscription: Subscription? = null

    private var resultSubscription: Subscription? = null

    private var itemSubscription: Subscription? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_inside_alert, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context = context ?: return
        val parentFragment = parentFragment ?: return

        itemSubscription?.let(Subscription::unsubscribe)
        itemSubscription = (parentFragment as AlertFragment)
                .onAlertItemChanged
                .asObservable()
                .subscribe({
                    alerts = it?.alerts?.filter { it.status.equals("CRITICAL") }
                    refresh()
                }, {
                    alerts = null
                    refresh()
                })

        val retry: Button = error.findViewById(R.id.retry_button) as Button
        retry.setOnClickListener {
            progress.visibility = View.VISIBLE
            error.visibility = View.GONE
            refresh()
        }

        swipeRefresh.setOnRefreshListener {
            refresh()
        }

        listView.setOnItemClickListener { _, _, i, _ ->
            val intent = AlertDetailActivity.createIntent(context, listView.adapter.getItem(i) as Alert)
            parentFragment.startActivityForResult(intent, CriticalAlertFragment.RequestCode)
        }

        resultSubscription?.let(Subscription::unsubscribe)
        (parentFragment as? AlertFragment)?.let {
            resultSubscription =
                    it.onCriticalAlertFragmentResult
                            .asObservable()
                            .filter { it }
                            .subscribe({
                                refresh()
                            }, {})
        }
    }

    private fun refresh() {
        swipeRefresh.isRefreshing = true
        subscription?.let(Subscription::unsubscribe)
        subscription = getAlert()
    }

    private fun getAlert(): Subscription {
        val context = context ?: return Subscriptions.empty()
        return viewModel
                .getAlerts(alerts, { it.status.equals("CRITICAL") })
                .subscribe({
                    listView.adapter = AlertAdapter(context, it)
                    swipeRefresh.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                    progress.visibility = View.GONE
                }, {
                    it.printStackTrace()
                    swipeRefresh.isRefreshing = false
                    error.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                })
    }

    override fun onDestroyView() {
        subscription?.let(Subscription::unsubscribe)
        resultSubscription?.let(Subscription::unsubscribe)
        itemSubscription?.let(Subscription::unsubscribe)
        super.onDestroyView()
    }
}
