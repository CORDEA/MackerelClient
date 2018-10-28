package jp.cordea.mackerelclient.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.fragment.MonitorFragment
import jp.cordea.mackerelclient.viewmodel.MonitorViewModel

@Module
interface MonitorFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            MonitorFragmentBindModule::class,
            MonitorViewModelModule::class
        ]
    )
    fun contributeMonitorFragment(): MonitorFragment
}

@Module
interface MonitorFragmentBindModule {
    @Binds
    fun bindFragment(fragment: MonitorFragment): Fragment

    @Binds
    fun bindViewModelStoreOwner(fragment: MonitorFragment): ViewModelStoreOwner
}

@Module
class MonitorViewModelModule : ViewModelModule<MonitorViewModel>(MonitorViewModel::class)
