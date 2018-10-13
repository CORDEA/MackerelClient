package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.MetricsActivity
import jp.cordea.mackerelclient.fragment.MetricsDeleteConfirmDialogFragment

@Module
interface MetricsActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            MetricsDeleteConfirmDialogFragmentModule::class
        ]
    )
    fun contributeMetricsActivity(): MetricsActivity
}

@Module
interface MetricsDeleteConfirmDialogFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    fun contributeMetricsDeleteConfirmDialogFragment(): MetricsDeleteConfirmDialogFragment
}
