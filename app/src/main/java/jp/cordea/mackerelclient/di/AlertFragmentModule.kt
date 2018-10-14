package jp.cordea.mackerelclient.di

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.AlertEventDispatcher
import jp.cordea.mackerelclient.AlertItemChangedSink
import jp.cordea.mackerelclient.AlertResultReceivedSink
import jp.cordea.mackerelclient.fragment.alert.AlertFragment

@Module
interface AlertFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            AlertFragmentBindModule::class
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
}
