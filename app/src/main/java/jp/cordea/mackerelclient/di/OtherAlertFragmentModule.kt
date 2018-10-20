package jp.cordea.mackerelclient.di

import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.OtherAlertEventDispatcher
import jp.cordea.mackerelclient.OtherAlertItemChangedSource
import jp.cordea.mackerelclient.OtherAlertResultReceivedSource
import jp.cordea.mackerelclient.fragment.alert.OtherAlertFragment
import jp.cordea.mackerelclient.viewmodel.AlertViewModel

@Module
interface OtherAlertFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            OtherAlertFragmentBindModule::class,
            AlertViewModelModule::class
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
}

@Module
class AlertViewModelModule : ViewModelModule<AlertViewModel>(AlertViewModel::class)
