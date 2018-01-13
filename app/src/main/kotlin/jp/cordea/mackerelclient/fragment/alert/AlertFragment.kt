package jp.cordea.mackerelclient.fragment.alert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ogaclejapan.rx.binding.RxEvent
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.AlertFragmentPagerAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Alerts
import kotterknife.bindView
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Yoshihiro Tanaka on 16/01/12.
 */
class AlertFragment : Fragment() {

    val onOtherAlertFragmentResult: RxEvent<Boolean> = RxEvent.create<Boolean>()

    val onCriticalAlertFragmentResult: RxEvent<Boolean> = RxEvent.create<Boolean>()

    val onAlertItemChanged: RxEvent<Alerts?> = RxEvent.create<Alerts?>()

    val viewPager: ViewPager by bindView(R.id.viewpager)

    val tabLayout: TabLayout by bindView(R.id.tab_layout)

    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_alert, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = AlertFragmentPagerAdapter(childFragmentManager, context)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        subscription?.unsubscribe()
        subscription = requestApi()
    }

    private fun requestApi(): Subscription {
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
        fun newInstance(): AlertFragment {
            val fragment = AlertFragment()
            return fragment
        }
    }
}
