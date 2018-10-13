package jp.cordea.mackerelclient.di

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.UserDeleteConfirmEventDispatcher
import jp.cordea.mackerelclient.UserDeleteConfirmSink
import jp.cordea.mackerelclient.UserDeleteConfirmSource
import jp.cordea.mackerelclient.activity.MainActivity
import jp.cordea.mackerelclient.fragment.HostFragment
import jp.cordea.mackerelclient.fragment.MonitorFragment
import jp.cordea.mackerelclient.fragment.ServiceFragment
import jp.cordea.mackerelclient.fragment.SettingFragment
import jp.cordea.mackerelclient.fragment.SettingStatusSelectionDialogFragment
import jp.cordea.mackerelclient.fragment.UserDeleteConfirmDialogFragment
import jp.cordea.mackerelclient.fragment.alert.AlertFragment
import jp.cordea.mackerelclient.fragment.alert.CriticalAlertFragment
import jp.cordea.mackerelclient.fragment.alert.OtherAlertFragment

@Module
interface MainActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            MainActivityBindModule::class,
            MainFragmentModule::class,
            UserFragmentModule::class
        ]
    )
    fun contributeMainActivity(): MainActivity
}

@Module
interface MainActivityBindModule {
    @Binds
    fun bindUserDeleteConfirmSink(
        dispatcher: UserDeleteConfirmEventDispatcher
    ): UserDeleteConfirmSink

    @Binds
    fun bindUserDeleteConfirmSource(
        dispatcher: UserDeleteConfirmEventDispatcher
    ): UserDeleteConfirmSource
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
    fun contributeUserDeleteConfirmDialogFragment(): UserDeleteConfirmDialogFragment
}
