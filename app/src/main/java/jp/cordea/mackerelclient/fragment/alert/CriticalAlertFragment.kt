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
import jp.cordea.mackerelclient.CriticalAlertItemChangedSource
import jp.cordea.mackerelclient.CriticalAlertResultReceivedSource
import jp.cordea.mackerelclient.adapter.AlertAdapter
import jp.cordea.mackerelclient.databinding.FragmentInsideAlertBinding
import jp.cordea.mackerelclient.view.CriticalAlertListItemModel
import jp.cordea.mackerelclient.viewmodel.AlertFragmentViewModel
import javax.inject.Inject

class CriticalAlertFragment : Fragment() {

    @Inject
    lateinit var viewModel: AlertFragmentViewModel

    @Inject
    lateinit var alertItemChangedSource: CriticalAlertItemChangedSource

    @Inject
    lateinit var alertResultReceivedSource: CriticalAlertResultReceivedSource

    @Inject
    lateinit var adapter: AlertAdapter

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
        binding.recyclerView.adapter = adapter

        viewModel.adapterItems
            .subscribeBy { adapter.update(it.map {
                CriticalAlertListItemModel(
                    it
                )
            }) }
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

        viewModel.start { it.status == "CRITICAL" }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val REQUEST_CODE = 1

        fun newInstance(): CriticalAlertFragment = CriticalAlertFragment()
    }
}
