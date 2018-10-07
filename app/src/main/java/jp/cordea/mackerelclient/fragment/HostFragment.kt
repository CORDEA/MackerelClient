package jp.cordea.mackerelclient.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.activity.HostDetailActivity
import jp.cordea.mackerelclient.adapter.HostAdapter
import jp.cordea.mackerelclient.databinding.FragmentHostBinding
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.viewmodel.HostViewModel
import rx.Subscription

class HostFragment : Fragment() {

    private var subscription: Subscription? = null

    private val viewModel by lazy { HostViewModel(context!!) }
    private val adapter by lazy { HostAdapter(this) }

    private lateinit var binding: FragmentHostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentHostBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(ListItemDecoration(context!!))

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
        subscription = getHosts(viewModel.displayHostState)
    }

    private fun getHosts(items: List<DisplayHostState>): Subscription {
        return viewModel
            .getHosts(items)
            .flatMap({ viewModel.getLatestMetrics(it) }, { hosts, tsdbs ->
                adapter.update(hosts.hosts, tsdbs.tsdbs)
            })
            .subscribe({
                binding.progressLayout.visibility = View.GONE
                binding.swipeRefresh.visibility = View.VISIBLE
                binding.swipeRefresh.isRefreshing = false
            }, {
                binding.swipeRefresh.isRefreshing = false
                binding.error.root.visibility = View.VISIBLE
                binding.progressLayout.visibility = View.GONE
                binding.swipeRefresh.visibility = View.GONE
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HostDetailActivity.REQUEST_CODE) {
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
        fun newInstance(): HostFragment = HostFragment()
    }
}
