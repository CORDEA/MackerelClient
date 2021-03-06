package jp.cordea.mackerelclient.navigator

import androidx.fragment.app.Fragment
import jp.cordea.mackerelclient.activity.AlertDetailActivity
import jp.cordea.mackerelclient.di.FragmentScope
import jp.cordea.mackerelclient.fragment.alert.CriticalAlertFragment
import jp.cordea.mackerelclient.model.DisplayableAlert
import javax.inject.Inject

@FragmentScope
class CriticalAlertNavigator @Inject constructor(
    private val fragment: Fragment
) {
    fun navigateToDetail(alert: DisplayableAlert) {
        val intent = AlertDetailActivity
            .createIntent(fragment.context!!, alert)
        fragment.parentFragment!!.startActivityForResult(intent, CriticalAlertFragment.REQUEST_CODE)
    }
}
