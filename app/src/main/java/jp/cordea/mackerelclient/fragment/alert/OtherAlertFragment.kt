package jp.cordea.mackerelclient.fragment.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.cordea.mackerelclient.activity.AlertDetailActivity
import jp.cordea.mackerelclient.adapter.OtherAlertAdapter
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.databinding.FragmentInsideAlertBinding
import jp.cordea.mackerelclient.viewmodel.AlertViewModel
import rx.Subscription
import rx.subscriptions.Subscriptions

class OtherAlertFragment : Fragment() {

    private val viewModel by lazy {
        AlertViewModel(context!!)
    }

    private var alerts: List<Alert>? = null

    private var subscription: Subscription? = null

    private var resultSubscription: Subscription? = null

    private var itemSubscription: Subscription? = null

    private lateinit var binding: FragmentInsideAlertBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentInsideAlertBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context = context ?: return
        val parentFragment = parentFragment ?: return

        itemSubscription?.unsubscribe()
        itemSubscription = (parentFragment as AlertFragment)
            .onAlertItemChanged
            .asObservable()
            .subscribe({ alert ->
                alerts = alert?.alerts?.filter { it.status != "CRITICAL" }
                refresh()
            }, {
                alerts = null
                refresh()
            })

        binding.error.retryButton.setOnClickListener {
            binding.progressLayout.visibility = View.VISIBLE
            binding.error.root.visibility = View.GONE
            refresh()
        }

        binding.swipeRefresh.setOnRefreshListener {
            refresh()
        }

        binding.listView.setOnItemClickListener { _, _, i, _ ->
            val intent = AlertDetailActivity
                .createIntent(context, binding.listView.adapter.getItem(i) as Alert)
            parentFragment.startActivityForResult(intent, OtherAlertFragment.REQUEST_CODE)
        }

        resultSubscription?.unsubscribe()
        (parentFragment as? AlertFragment)?.let { fragment ->
            resultSubscription =
                fragment.onOtherAlertFragmentResult
                    .asObservable()
                    .filter { it }
                    .subscribe({
                        refresh()
                    }, {})
        }
    }

    private fun refresh() {
        binding.swipeRefresh.isRefreshing = true
        subscription?.unsubscribe()
        subscription = getAlert()
    }

    private fun getAlert(): Subscription {
        val context = context ?: return Subscriptions.empty()
        return viewModel
            .getAlerts(alerts) { it.status != "CRITICAL" }
            .subscribe({
                binding.listView.adapter = OtherAlertAdapter(context, it)
                binding.swipeRefresh.isRefreshing = false
                binding.swipeRefresh.visibility = View.VISIBLE
                binding.progressLayout.visibility = View.GONE
            }, {
                it.printStackTrace()
                binding.swipeRefresh.isRefreshing = false
                binding.error.root.visibility = View.VISIBLE
                binding.progressLayout.visibility = View.GONE
            })
    }

    override fun onDestroyView() {
        subscription?.unsubscribe()
        resultSubscription?.unsubscribe()
        itemSubscription?.unsubscribe()
        super.onDestroyView()
    }

    companion object {

        const val REQUEST_CODE = 0

        fun newInstance(): OtherAlertFragment = OtherAlertFragment()
    }
}
