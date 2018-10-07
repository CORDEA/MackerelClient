package jp.cordea.mackerelclient.fragment.alert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import jp.cordea.mackerelclient.adapter.AlertFragmentPagerAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Alerts
import jp.cordea.mackerelclient.databinding.FragmentAlertBinding
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment() {

    val onOtherAlertFragmentResult = PublishSubject.create<Boolean>()
    val onCriticalAlertFragmentResult = PublishSubject.create<Boolean>()
    val onAlertItemChanged = PublishSubject.create<Alerts>()

    private val disposable = SerialDisposable()

    private lateinit var binding: FragmentAlertBinding

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

        requestApi()
    }

    private fun requestApi() {
        val context = context!!
        MackerelApiClient
            .getAlerts(context)
            .delay(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onAlertItemChanged.onNext(it)
            }, {
                onAlertItemChanged.onError(it)
            })
            .run(disposable::set)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CriticalAlertFragment.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    onCriticalAlertFragmentResult.onNext(true)
                }
            }
            OtherAlertFragment.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    onOtherAlertFragmentResult.onNext(true)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    companion object {
        fun newInstance(): AlertFragment = AlertFragment()
    }
}
