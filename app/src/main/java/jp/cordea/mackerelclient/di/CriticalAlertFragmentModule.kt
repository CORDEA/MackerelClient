package jp.cordea.mackerelclient.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.CriticalAlertEventDispatcher
import jp.cordea.mackerelclient.CriticalAlertItemChangedSource
import jp.cordea.mackerelclient.CriticalAlertResultReceivedSource
import jp.cordea.mackerelclient.fragment.alert.CriticalAlertFragment

@Module
interface CriticalAlertFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            CriticalAlertFragmentBindModule::class,
            AlertFragmentViewModelModule::class
        ]
    )
    fun contributeCriticalAlertFragment(): CriticalAlertFragment
}

@Module
interface CriticalAlertFragmentBindModule {
    @Binds
    fun bindCriticalAlertItemChangedSource(
        dispatcher: CriticalAlertEventDispatcher
    ): CriticalAlertItemChangedSource

    @Binds
    fun bindCriticalAlertResultReceivedSource(
        dispatcher: CriticalAlertEventDispatcher
    ): CriticalAlertResultReceivedSource

    @Binds
    fun bindViewModelStoreOwner(fragment: CriticalAlertFragment): ViewModelStoreOwner

    @Binds
    fun bindFragment(fragment: CriticalAlertFragment): Fragment
}
