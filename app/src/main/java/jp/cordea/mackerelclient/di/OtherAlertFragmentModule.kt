package jp.cordea.mackerelclient.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.OtherAlertEventDispatcher
import jp.cordea.mackerelclient.OtherAlertItemChangedSource
import jp.cordea.mackerelclient.OtherAlertResultReceivedSource
import jp.cordea.mackerelclient.fragment.alert.OtherAlertFragment
import jp.cordea.mackerelclient.viewmodel.AlertFragmentViewModel

@Module
interface OtherAlertFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            OtherAlertFragmentBindModule::class,
            AlertFragmentViewModelModule::class
        ]
    )
    fun contributeOtherAlertFragment(): OtherAlertFragment
}

@Module
interface OtherAlertFragmentBindModule {
    @Binds
    fun bindOtherAlertItemChangedSource(
        dispatcher: OtherAlertEventDispatcher
    ): OtherAlertItemChangedSource

    @Binds
    fun bindOtherAlertResultReceivedSource(
        dispatcher: OtherAlertEventDispatcher
    ): OtherAlertResultReceivedSource

    @Binds
    fun bindViewModelStoreOwner(fragment: OtherAlertFragment): ViewModelStoreOwner

    @Binds
    fun bindFragment(fragment: OtherAlertFragment): Fragment
}

@Module
class AlertFragmentViewModelModule : ViewModelModule<AlertFragmentViewModel>(AlertFragmentViewModel::class)
