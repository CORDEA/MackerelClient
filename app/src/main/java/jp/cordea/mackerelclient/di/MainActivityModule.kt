package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.MainActivity
import jp.cordea.mackerelclient.fragment.ServiceFragment
import jp.cordea.mackerelclient.fragment.SettingFragment
import jp.cordea.mackerelclient.fragment.SettingStatusSelectionDialogFragment

@Module
interface MainActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            MainFragmentModule::class,
            HostFragmentModule::class,
            UserFragmentModule::class,
            AlertFragmentModule::class,
            MonitorFragmentModule::class,
            OtherAlertFragmentModule::class,
            CriticalAlertFragmentModule::class,
            UserDeleteConfirmDialogFragmentModule::class
        ]
    )
    fun contributeMainActivity(): MainActivity
}

@Module
interface MainFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    fun contributeServiceFragment(): ServiceFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeSettingFragment(): SettingFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeSettingStatusSelectionDialogFragment(): SettingStatusSelectionDialogFragment
}
