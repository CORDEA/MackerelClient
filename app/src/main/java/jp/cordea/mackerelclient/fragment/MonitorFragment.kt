package jp.cordea.mackerelclient.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import jp.cordea.mackerelclient.activity.MonitorDetailActivity
import jp.cordea.mackerelclient.adapter.MonitorAdapter
import jp.cordea.mackerelclient.databinding.FragmentMonitorBinding
import jp.cordea.mackerelclient.viewmodel.MonitorViewModel
import javax.inject.Inject

class MonitorFragment : Fragment() {
    @Inject
    lateinit var viewModel: MonitorViewModel

    @Inject
    lateinit var adapter: MonitorAdapter

    private val compositeDisposable = CompositeDisposable()

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
        binding.recyclerView.adapter = adapter

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

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        binding.error.retryButton.setOnClickListener {
            viewModel.clickedRetryButton()
        }

        viewModel.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MonitorDetailActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.refresh()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        fun newInstance(): MonitorFragment = MonitorFragment()
    }
}
