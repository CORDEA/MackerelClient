package jp.cordea.mackerelclient.di

import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.MetricsActivity
import jp.cordea.mackerelclient.fragment.MetricsDeleteConfirmDialogFragment
import jp.cordea.mackerelclient.viewmodel.MetricsViewModel

@Module
interface MetricsActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            MetricsActivityBindModule::class,
            MetricsViewModelModule::class,
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

@Module
interface MetricsActivityBindModule {
    @Binds
    fun bindViewModelStoreOwner(activity: MetricsActivity): ViewModelStoreOwner
}

@Module
class MetricsViewModelModule :
    ViewModelModule<MetricsViewModel>(MetricsViewModel::class)
