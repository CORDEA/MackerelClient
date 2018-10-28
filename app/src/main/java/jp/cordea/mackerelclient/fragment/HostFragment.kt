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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.activity.HostDetailActivity
import jp.cordea.mackerelclient.adapter.HostAdapter
import jp.cordea.mackerelclient.databinding.FragmentHostBinding
import jp.cordea.mackerelclient.model.DisplayableHost
import jp.cordea.mackerelclient.viewmodel.HostViewModel
import javax.inject.Inject

class HostFragment : Fragment() {
    @Inject
    lateinit var viewModel: HostViewModel

    private val adapter by lazy { HostAdapter(this) }

    private val compositeDisposable = CompositeDisposable()

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

        viewModel.adapterItems
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { pair ->
                adapter.update(
                    pair.first.hosts.map { DisplayableHost.from(it) },
                    pair.second.tsdbs
                )
            }
            .addTo(compositeDisposable)

        viewModel.isProgressLayoutVisible
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { binding.progressLayout.isVisible = it }
            .addTo(compositeDisposable)

        viewModel.isSwipeRefreshLayoutVisible
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { binding.swipeRefresh.isVisible = it }
            .addTo(compositeDisposable)

        viewModel.isErrorLayoutVisible
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { binding.error.root.isVisible = it }
            .addTo(compositeDisposable)

        viewModel.isRefreshing
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { binding.swipeRefresh.isRefreshing = it }
            .addTo(compositeDisposable)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh(true)
        }
        binding.error.retryButton.setOnClickListener {
            viewModel.clickedRetryButton()
        }

        viewModel.refresh(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HostDetailActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.refresh(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        fun newInstance(): HostFragment = HostFragment()
    }
}
