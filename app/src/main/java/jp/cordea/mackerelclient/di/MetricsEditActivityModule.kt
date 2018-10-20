package jp.cordea.mackerelclient.di

import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.MetricsEditActivity
import jp.cordea.mackerelclient.viewmodel.MetricsEditViewModel

@Module
interface MetricsEditActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            MetricsEditActivityBindModule::class,
            MetricsEditViewModelModule::class
        ]
    )
    fun contributeMetricsEditActivity(): MetricsEditActivity
}

@Module
interface MetricsEditActivityBindModule {
    @Binds
    fun bindViewModelStoreOwner(activity: MetricsEditActivity): ViewModelStoreOwner
}

@Module
class MetricsEditViewModelModule :
    ViewModelModule<MetricsEditViewModel>(MetricsEditViewModel::class)
