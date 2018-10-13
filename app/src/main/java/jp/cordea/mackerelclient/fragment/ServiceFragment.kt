package jp.cordea.mackerelclient.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.SerialDisposable
import jp.cordea.mackerelclient.activity.ServiceMetricsActivity
import jp.cordea.mackerelclient.adapter.ServiceAdapter
import jp.cordea.mackerelclient.api.response.Service
import jp.cordea.mackerelclient.databinding.FragmentServiceBinding
import jp.cordea.mackerelclient.viewmodel.ServiceViewModel

class ServiceFragment : Fragment() {

    private val viewModel by lazy { ServiceViewModel(context!!) }

    private val disposable = SerialDisposable()

    private lateinit var binding: FragmentServiceBinding

    private var services: List<Service>? = null

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
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
        getServices()
    }

    private fun getServices() {
        val context = context!!
        viewModel
            .getServices()
            .subscribe({
                binding.swipeRefresh.isRefreshing = false
                binding.listView.adapter = ServiceAdapter(context, it.services)
                binding.swipeRefresh.visibility = View.VISIBLE
                binding.progressLayout.visibility = View.GONE
                services = it.services
            }, {
                binding.swipeRefresh.isRefreshing = false
                binding.error.root.visibility = View.VISIBLE
                binding.progressLayout.visibility = View.GONE
            })
            .run(disposable::set)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    companion object {
        fun newInstance(): ServiceFragment = ServiceFragment()
    }
}
