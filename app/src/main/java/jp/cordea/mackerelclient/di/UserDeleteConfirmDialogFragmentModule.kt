package jp.cordea.mackerelclient.di

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.UserDeleteConfirmEventDispatcher
import jp.cordea.mackerelclient.UserDeleteConfirmSink
import jp.cordea.mackerelclient.fragment.UserDeleteConfirmDialogFragment

@Module
interface UserDeleteConfirmDialogFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            UserDeleteConfirmDialogFragmentBindModule::class
        ]
    )
    fun contributeUserDeleteConfirmDialogFragment(): UserDeleteConfirmDialogFragment
}

@Module
interface UserDeleteConfirmDialogFragmentBindModule {
    @Binds
    fun bindUserDeleteConfirmSink(dispatcher: UserDeleteConfirmEventDispatcher): UserDeleteConfirmSink
}
