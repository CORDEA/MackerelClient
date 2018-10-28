package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.fragment.MetricsDeleteConfirmDialogFragment

@Module
interface MetricsDeleteConfirmDialogFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    fun contributeMetricsDeleteConfirmDialogFragment(): MetricsDeleteConfirmDialogFragment
}
