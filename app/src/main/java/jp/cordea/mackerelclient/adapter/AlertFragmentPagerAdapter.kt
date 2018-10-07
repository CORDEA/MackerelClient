package jp.cordea.mackerelclient.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.fragment.alert.CriticalAlertFragment
import jp.cordea.mackerelclient.fragment.alert.OtherAlertFragment

class AlertFragmentPagerAdapter(
    manager: FragmentManager,
    val context: Context
) : FragmentPagerAdapter(manager) {

    override fun getCount(): Int =
        2

    override fun getItem(position: Int): Fragment? =
        when (position) {
            0 -> CriticalAlertFragment.newInstance()
            1 -> OtherAlertFragment.newInstance()
            else -> null
        }

    override fun getPageTitle(position: Int): CharSequence? =
        when (position) {
            0 -> context.getString(R.string.in_progress_alert_title)
            1 -> context.getString(R.string.closed_alert_title)
            else -> null
        }
}
