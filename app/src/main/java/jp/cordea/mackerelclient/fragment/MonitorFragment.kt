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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import jp.cordea.mackerelclient.activity.MonitorDetailActivity
import jp.cordea.mackerelclient.adapter.MonitorAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.databinding.FragmentMonitorBinding

class MonitorFragment : Fragment() {

    private val disposable = SerialDisposable()

    private lateinit var binding: FragmentMonitorBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

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
        requestApi()
    }

    private fun requestApi() {
        val context = context!!
        MackerelApiClient
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
                    pairs.add(type to null)
                    items.map { pairs.add(type to it) }
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
            .run(disposable::set)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MonitorDetailActivity.REQUEST_CODE) {
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
        fun newInstance(): MonitorFragment = MonitorFragment()
    }
}
