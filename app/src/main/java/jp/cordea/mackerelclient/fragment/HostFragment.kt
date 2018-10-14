package jp.cordea.mackerelclient.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.SerialDisposable
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.activity.HostDetailActivity
import jp.cordea.mackerelclient.adapter.HostAdapter
import jp.cordea.mackerelclient.databinding.FragmentHostBinding
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.viewmodel.HostViewModel
import javax.inject.Inject

class HostFragment : Fragment() {

    @Inject
    lateinit var viewModel: HostViewModel

    private val disposable = SerialDisposable()

    private val adapter by lazy { HostAdapter(this) }

    private lateinit var binding: FragmentHostBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

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
        getHosts(viewModel.displayHostState)
    }

    private fun getHosts(items: List<DisplayHostState>) {
        viewModel
            .getHosts(items)
            .flatMap({ viewModel.getLatestMetrics(it).toMaybe() }, { hosts, tsdbs ->
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
            .run(disposable::set)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HostDetailActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                refresh()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    companion object {
        fun newInstance(): HostFragment = HostFragment()
    }
}
