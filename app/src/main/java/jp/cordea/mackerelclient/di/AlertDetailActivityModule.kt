package jp.cordea.mackerelclient.di

import androidx.fragment.app.FragmentManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.AlertDetailActivity
import jp.cordea.mackerelclient.fragment.AlertCloseConfirmDialogFragment
import jp.cordea.mackerelclient.fragment.AlertCloseDialogFragment
import jp.cordea.mackerelclient.fragment.AlertCloseDialogListener
import jp.cordea.mackerelclient.fragment.AlertCloseDialogLoader
import jp.cordea.mackerelclient.fragment.AlertCloseDialogLoaderImpl

@Module
interface AlertDetailActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            AlertDetailActivityBindModule::class,
            AlertCloseConfirmDialogFragmentModule::class,
            AlertCloseDialogFragmentModule::class
        ]
    )
    fun contributeAlertDetailActivity(): AlertDetailActivity
}

@Module
abstract class AlertDetailActivityBindModule {
    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideFragmentManager(activity: AlertDetailActivity): FragmentManager =
            activity.supportFragmentManager
    }

    @Binds
    abstract fun bindAlertCloseDialogLoader(loader: AlertCloseDialogLoaderImpl): AlertCloseDialogLoader

    @Binds
    abstract fun bindAlertCloseDialogListener(loader: AlertCloseDialogLoaderImpl): AlertCloseDialogListener
}

@Module
interface AlertCloseConfirmDialogFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    fun contributeAlertCloseConfirmDialogFragment(): AlertCloseConfirmDialogFragment
}

@Module
interface AlertCloseDialogFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    fun contributeAlertCloseDialogFragment(): AlertCloseDialogFragment
}
