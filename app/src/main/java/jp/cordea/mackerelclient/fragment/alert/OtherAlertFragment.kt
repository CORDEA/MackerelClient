package jp.cordea.mackerelclient.fragment.alert

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.cordea.mackerelclient.OtherAlertItemChangedSource
import jp.cordea.mackerelclient.OtherAlertResultReceivedSource
import jp.cordea.mackerelclient.activity.AlertDetailActivity
import jp.cordea.mackerelclient.adapter.OtherAlertAdapter
import jp.cordea.mackerelclient.databinding.FragmentInsideAlertBinding
import jp.cordea.mackerelclient.viewmodel.AlertViewModel
import javax.inject.Inject

class OtherAlertFragment : Fragment() {

    @Inject
    lateinit var viewModel: AlertViewModel

    @Inject
    lateinit var adapter: OtherAlertAdapter

    @Inject
    lateinit var alertItemChangedSource: OtherAlertItemChangedSource

    @Inject
    lateinit var alertResultReceivedSource: OtherAlertResultReceivedSource

    private val compositeDisposable = CompositeDisposable()

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
        binding.listView.adapter = adapter

        viewModel.adapterItems
            .subscribeBy { adapter.update(it) }
            .addTo(compositeDisposable)

        viewModel.isRefreshing
            .subscribeBy { binding.swipeRefresh.isRefreshing = it }
            .addTo(compositeDisposable)

        viewModel.isSwipeRefreshLayoutVisible
            .subscribeBy { binding.swipeRefresh.isVisible = it }
            .addTo(compositeDisposable)

        viewModel.isProgressLayoutVisible
            .subscribeBy { binding.progressLayout.isVisible = it }
            .addTo(compositeDisposable)

        viewModel.isErrorVisible
            .subscribeBy { binding.error.root.isVisible = it }
            .addTo(compositeDisposable)

        alertItemChangedSource
            .onAlertItemChanged()
            .subscribe({
                viewModel.updateCache(it)
                viewModel.refresh(false)
            }, {
                viewModel.clearCache()
                viewModel.refresh(false)
            })
            .addTo(compositeDisposable)

        alertResultReceivedSource
            .onAlertResultReceived()
            .subscribeBy { viewModel.refresh(false) }
            .addTo(compositeDisposable)

        binding.error.retryButton.setOnClickListener {
            viewModel.clickedRetryButton()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh(true)
        }

        binding.listView.setOnItemClickListener { _, _, i, _ ->
            val intent = AlertDetailActivity
                .createIntent(context, adapter.getItem(i))
            parentFragment.startActivityForResult(intent, OtherAlertFragment.REQUEST_CODE)
        }

        viewModel.start { it.status != "CRITICAL" }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val REQUEST_CODE = 0

        fun newInstance(): OtherAlertFragment = OtherAlertFragment()
    }
}
