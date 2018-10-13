package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.MonitorDetailActivity
import jp.cordea.mackerelclient.fragment.MonitorSettingDeleteDialogFragment

@Module
interface MonitorDetailActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            MonitorSettingDeleteDialogFragmentModule::class
        ]
    )
    fun contributeMonitorDetailActivity(): MonitorDetailActivity
}

@Module
interface MonitorSettingDeleteDialogFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    fun contributeMonitorSettingDeleteDialogFragment(): MonitorSettingDeleteDialogFragment
}
