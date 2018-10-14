package jp.cordea.mackerelclient.di

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.OtherAlertEventDispatcher
import jp.cordea.mackerelclient.OtherAlertItemChangedSource
import jp.cordea.mackerelclient.OtherAlertResultReceivedSource
import jp.cordea.mackerelclient.fragment.alert.OtherAlertFragment

@Module
interface OtherAlertFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            OtherAlertFragmentBindModule::class
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
}
