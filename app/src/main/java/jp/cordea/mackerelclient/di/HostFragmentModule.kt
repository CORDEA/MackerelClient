package jp.cordea.mackerelclient.di

import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.fragment.HostFragment
import jp.cordea.mackerelclient.viewmodel.HostViewModel

@Module
interface HostFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            HostFragmentBindModule::class,
            HostViewModelModule::class
        ]
    )
    fun contributeHostFragment(): HostFragment
}

@Module
interface HostFragmentBindModule {
    @Binds
    fun bindViewModelStoreOwner(fragment: HostFragment): ViewModelStoreOwner
}

@Module
class HostViewModelModule :
    ViewModelModule<HostViewModel>(HostViewModel::class)
