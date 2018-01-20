package jp.cordea.mackerelclient.fragment.alert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ogaclejapan.rx.binding.RxEvent
import jp.cordea.mackerelclient.adapter.AlertFragmentPagerAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Alerts
import jp.cordea.mackerelclient.databinding.FragmentAlertBinding
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.Subscriptions
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment() {

    val onOtherAlertFragmentResult: RxEvent<Boolean> = RxEvent.create<Boolean>()

    val onCriticalAlertFragmentResult: RxEvent<Boolean> = RxEvent.create<Boolean>()

    val onAlertItemChanged: RxEvent<Alerts?> = RxEvent.create<Alerts?>()

    private var subscription: Subscription? = null

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

        subscription?.unsubscribe()
        subscription = requestApi()
    }

    private fun requestApi(): Subscription {
        val context = context ?: return Subscriptions.empty()
        return MackerelApiClient
                .getAlerts(context)
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onAlertItemChanged.post(it)
                }, {
                    onAlertItemChanged.post(null)
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CriticalAlertFragment.RequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    onCriticalAlertFragmentResult.post(true)
                }
            }
            OtherAlertFragment.RequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    onOtherAlertFragmentResult.post(true)
                }
            }
        }
    }

    override fun onDestroyView() {
        subscription?.let(Subscription::unsubscribe)
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): AlertFragment =
                AlertFragment()
    }
}
