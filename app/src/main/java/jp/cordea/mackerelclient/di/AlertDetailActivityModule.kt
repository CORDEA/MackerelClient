package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.AlertDetailActivity
import jp.cordea.mackerelclient.fragment.AlertCloseDialogFragment

@Module
interface AlertDetailActivityModule {
    @ContributesAndroidInjector(
        modules = [
            AlertCloseDialogFragmentModule::class
        ]
    )
    fun contributeAlertDetailActivity(): AlertDetailActivity
}

@Module
interface AlertCloseDialogFragmentModule {
    @ContributesAndroidInjector
    fun contributeAlertCloseDialogFragment(): AlertCloseDialogFragment
}
