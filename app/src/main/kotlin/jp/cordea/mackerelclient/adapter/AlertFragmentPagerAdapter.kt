package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.fragment.alert.CriticalAlertFragment
import jp.cordea.mackerelclient.fragment.alert.OtherAlertFragment

/**
 * Created by Yoshihiro Tanaka on 16/01/13.
 */
class AlertFragmentPagerAdapter(fm: FragmentManager, val context: Context) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return CriticalAlertFragment.newInstance()
            1 -> return OtherAlertFragment.newInstance()
        }
        return null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return context.getString(R.string.in_progress_alert_title)
            1 -> return context.getString(R.string.closed_alert_title)
        }
        return null
    }
}