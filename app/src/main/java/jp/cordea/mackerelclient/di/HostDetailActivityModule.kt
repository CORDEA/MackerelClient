package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.HostDetailActivity
import jp.cordea.mackerelclient.fragment.HostRetireDialogFragment

@Module
interface HostDetailActivityModule {
    @ContributesAndroidInjector(
        modules = [
            HostRetireDialogFragmentModule::class
        ]
    )
    fun contributeHostDetailActivity(): HostDetailActivity
}

@Module
interface HostRetireDialogFragmentModule {
    @ContributesAndroidInjector
    fun contributeHostRetireDialogFragment(): HostRetireDialogFragment
}
