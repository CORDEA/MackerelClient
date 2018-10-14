package jp.cordea.mackerelclient.fragment.alert

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.SerialDisposable
import jp.cordea.mackerelclient.activity.AlertDetailActivity
import jp.cordea.mackerelclient.adapter.AlertAdapter
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.databinding.FragmentInsideAlertBinding
import jp.cordea.mackerelclient.viewmodel.AlertViewModel
import javax.inject.Inject

class CriticalAlertFragment : Fragment() {

    @Inject
    lateinit var viewModel: AlertViewModel

    private var alerts: List<Alert>? = null

    private val disposable = SerialDisposable()
    private val itemDisposable = SerialDisposable()
    private val resultDisposable = SerialDisposable()

    private lateinit var binding: FragmentInsideAlertBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

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

        (parentFragment as AlertFragment)
            .onAlertItemChanged
            .subscribe({
                alerts = it?.alerts?.filter { it.status.equals("CRITICAL") }
                refresh()
            }, {
                alerts = null
                refresh()
            })
            .run(itemDisposable::set)

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
            parentFragment.startActivityForResult(intent, CriticalAlertFragment.REQUEST_CODE)
        }

        (parentFragment as? AlertFragment)?.let { fragment ->
            fragment.onCriticalAlertFragmentResult
                .filter { it }
                .subscribe({
                    refresh()
                }, {})
                .run(resultDisposable::set)
        }
    }

    private fun refresh() {
        binding.swipeRefresh.isRefreshing = true
        getAlert()
    }

    private fun getAlert() {
        val context = context!!
        viewModel
            .getAlerts(alerts) { it.status.equals("CRITICAL") }
            .subscribe({
                binding.listView.adapter = AlertAdapter(context, it)
                binding.swipeRefresh.visibility = View.VISIBLE
                binding.swipeRefresh.isRefreshing = false
                binding.progressLayout.visibility = View.GONE
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
        resultDisposable.dispose()
        itemDisposable.dispose()
    }

    companion object {
        const val REQUEST_CODE = 1

        fun newInstance(): CriticalAlertFragment = CriticalAlertFragment()
    }
}
