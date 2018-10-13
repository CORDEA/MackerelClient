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
    @ContributesAndroidInjector(
        modules = [
            MainFragmentModule::class
        ]
    )
    fun contributeMainActivity(): MainActivity
}

@Module
interface MainFragmentModule {
    @ContributesAndroidInjector
    fun contributeAlertFragment(): AlertFragment

    @ContributesAndroidInjector
    fun contributeCriticalAlertFragment(): CriticalAlertFragment

    @ContributesAndroidInjector
    fun contributeOtherAlertFragment(): OtherAlertFragment

    @ContributesAndroidInjector
    fun contributeHostFragment(): HostFragment

    @ContributesAndroidInjector
    fun contributeMonitorFragment(): MonitorFragment

    @ContributesAndroidInjector
    fun contributeServiceFragment(): ServiceFragment

    @ContributesAndroidInjector
    fun contributeSettingFragment(): SettingFragment

    @ContributesAndroidInjector
    fun contributeSettingStatusSelectionDialogFragment(): SettingStatusSelectionDialogFragment

    @ContributesAndroidInjector
    fun contributeUserFragment(): UserFragment
}
