package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.MonitorDetailActivity

@Module
interface MonitorDetailActivityModule {
    @ContributesAndroidInjector
    fun contributeMonitorDetailActivity(): MonitorDetailActivity
}
