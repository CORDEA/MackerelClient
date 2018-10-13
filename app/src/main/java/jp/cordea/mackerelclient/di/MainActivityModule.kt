package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.MainActivity
import jp.cordea.mackerelclient.fragment.HostFragment
import jp.cordea.mackerelclient.fragment.MonitorFragment
import jp.cordea.mackerelclient.fragment.ServiceFragment
import jp.cordea.mackerelclient.fragment.SettingFragment
import jp.cordea.mackerelclient.fragment.SettingStatusSelectionDialogFragment
import jp.cordea.mackerelclient.fragment.UserFragment
import jp.cordea.mackerelclient.fragment.alert.AlertFragment
import jp.cordea.mackerelclient.fragment.alert.CriticalAlertFragment
import jp.cordea.mackerelclient.fragment.alert.OtherAlertFragment

@Module
interface MainActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            MainFragmentModule::class
        ]
    )
    fun contributeMainActivity(): MainActivity
}

@Module
interface MainFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    fun contributeAlertFragment(): AlertFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeCriticalAlertFragment(): CriticalAlertFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeOtherAlertFragment(): OtherAlertFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeHostFragment(): HostFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeMonitorFragment(): MonitorFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeServiceFragment(): ServiceFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeSettingFragment(): SettingFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeSettingStatusSelectionDialogFragment(): SettingStatusSelectionDialogFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeUserFragment(): UserFragment
}
