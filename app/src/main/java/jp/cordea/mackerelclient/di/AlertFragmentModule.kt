package jp.cordea.mackerelclient.di

import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.AlertEventDispatcher
import jp.cordea.mackerelclient.AlertItemChangedSink
import jp.cordea.mackerelclient.AlertResultReceivedSink
import jp.cordea.mackerelclient.fragment.alert.AlertFragment
import jp.cordea.mackerelclient.viewmodel.AlertViewModel

@Module
interface AlertFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            AlertFragmentBindModule::class,
            AlertViewModelModule::class
        ]
    )
    fun contributeAlertFragment(): AlertFragment
}

@Module
interface AlertFragmentBindModule {
    @Binds
    fun bindAlertItemChangedSink(dispatcher: AlertEventDispatcher): AlertItemChangedSink

    @Binds
    fun bindAlertResultReceivedSink(
        dispatcher: AlertEventDispatcher
    ): AlertResultReceivedSink

    @Binds
    fun bindViewModelStoreOwner(fragment: AlertFragment): ViewModelStoreOwner
}

@Module
class AlertViewModelModule : ViewModelModule<AlertViewModel>(AlertViewModel::class)
