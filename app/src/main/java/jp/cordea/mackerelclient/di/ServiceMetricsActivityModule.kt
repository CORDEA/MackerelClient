package jp.cordea.mackerelclient.di

import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.ServiceMetricsActivity
import jp.cordea.mackerelclient.viewmodel.ServiceMetricsViewModel

@Module
interface ServiceMetricsActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            ServiceMetricsActivityBindModule::class,
            ServiceMetricsViewModelModule::class
        ]
    )
    fun contributeServiceMetricsActivity(): ServiceMetricsActivity
}

@Module
interface ServiceMetricsActivityBindModule {
    @Binds
    fun bindViewModelStoreOwner(activity: ServiceMetricsActivity): ViewModelStoreOwner
}

@Module
class ServiceMetricsViewModelModule :
    ViewModelModule<ServiceMetricsViewModel>(ServiceMetricsViewModel::class)
