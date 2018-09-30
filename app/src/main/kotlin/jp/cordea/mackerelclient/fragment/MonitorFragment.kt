package jp.cordea.mackerelclient.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.cordea.mackerelclient.activity.MonitorDetailActivity
import jp.cordea.mackerelclient.adapter.MonitorAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.databinding.FragmentMonitorBinding
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.Subscriptions

class MonitorFragment : Fragment() {

    private var subscription: Subscription? = null

    private lateinit var binding: FragmentMonitorBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View =
            FragmentMonitorBinding.inflate(inflater, container, false).also {
                binding = it
            }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        refresh()

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
        binding.swipeRefresh.isRefreshing = true
        subscription?.unsubscribe()
        subscription = requestApi()
    }

    private fun requestApi(): Subscription {
        val context = context ?: return Subscriptions.empty()
        return MackerelApiClient
                .getMonitors(context)
                .map { it.monitors }
                .map { monitors ->
                    val sections = monitors
                            .asSequence()
                            .map { it.type }
                            .distinct()
                            .sortedBy { it }
                    val pairs: MutableList<Pair<String, Monitor?>> = arrayListOf()
                    for (section in sections) {
                        val items = monitors.filter { section == it.type }
                        val type = items[0].type
                        pairs.add(Pair(type, null))
                        items.map { pairs.add(Pair(type, it)) }
                    }
                    pairs
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.swipeRefresh.isRefreshing = false
                    binding.recyclerView.adapter = MonitorAdapter(this, it)
                    binding.progressLayout.visibility = View.GONE
                    binding.swipeRefresh.visibility = View.VISIBLE
                }, {
                    binding.swipeRefresh.isRefreshing = false
                    binding.error.root.visibility = View.VISIBLE
                    binding.progressLayout.visibility = View.GONE
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MonitorDetailActivity.REQUEST_CODE) {
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
        fun newInstance(): MonitorFragment = MonitorFragment()
    }
}
