package jp.cordea.mackerelclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.cordea.mackerelclient.activity.ServiceMetricsActivity
import jp.cordea.mackerelclient.adapter.ServiceAdapter
import jp.cordea.mackerelclient.api.response.Service
import jp.cordea.mackerelclient.databinding.FragmentServiceBinding
import jp.cordea.mackerelclient.viewmodel.ServiceViewModel
import rx.Subscription
import rx.subscriptions.Subscriptions

class ServiceFragment : Fragment() {

    private lateinit var binding: FragmentServiceBinding

    private var services: List<Service>? = null

    private var subscription: Subscription? = null

    private val viewModel by lazy {
        ServiceViewModel(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentServiceBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context = context ?: return

        refresh()

        binding.listView.setOnItemClickListener { _, _, i, _ ->
            services?.let {
                startActivity(ServiceMetricsActivity.createIntent(context, it[i].name))
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            refresh()
        }

        binding.error.retryButton.setOnClickListener {
            binding.progressLayout.visibility = View.VISIBLE
            binding.error.root.visibility = View.GONE
            refresh()
        }
    }

    private fun refresh() {
        subscription?.unsubscribe()
        subscription = getServices()
    }

    private fun getServices(): Subscription {
        val context = context ?: return Subscriptions.empty()
        return viewModel
            .getServices()
            .subscribe({
                binding.swipeRefresh.isRefreshing = false
                binding.listView.adapter = ServiceAdapter(context, it.services)
                binding.swipeRefresh.visibility = View.VISIBLE
                binding.progressLayout.visibility = View.GONE
                services = it.services
            }, {
                it.printStackTrace()
                binding.swipeRefresh.isRefreshing = false
                binding.error.root.visibility = View.VISIBLE
                binding.progressLayout.visibility = View.GONE
            })
    }

    override fun onDestroyView() {
        subscription?.unsubscribe()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): ServiceFragment = ServiceFragment()
    }
}
