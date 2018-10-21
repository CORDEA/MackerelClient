package jp.cordea.mackerelclient.fragment.alert

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import jp.cordea.mackerelclient.AlertItemChangedSink
import jp.cordea.mackerelclient.AlertResultReceivedSink
import jp.cordea.mackerelclient.adapter.AlertFragmentPagerAdapter
import jp.cordea.mackerelclient.databinding.FragmentAlertBinding
import jp.cordea.mackerelclient.viewmodel.AlertViewModel
import javax.inject.Inject

class AlertFragment : Fragment() {

    @Inject
    lateinit var viewModel: AlertViewModel

    @Inject
    lateinit var alertItemChangedSink: AlertItemChangedSink

    @Inject
    lateinit var alertResultReceivedSink: AlertResultReceivedSink

    private val compositeDisposable = CompositeDisposable()

    private lateinit var binding: FragmentAlertBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentAlertBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context = context ?: return
        val adapter = AlertFragmentPagerAdapter(childFragmentManager, context)
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        viewModel.items
            .subscribe({
                alertItemChangedSink.notifyAlertItemChanged(it)
            }, {
                alertItemChangedSink.notifyAlertItemChanged(it)
            })
            .addTo(compositeDisposable)

        viewModel.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        alertResultReceivedSink.notifyAlertResultReceived(requestCode)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        fun newInstance(): AlertFragment = AlertFragment()
    }
}
