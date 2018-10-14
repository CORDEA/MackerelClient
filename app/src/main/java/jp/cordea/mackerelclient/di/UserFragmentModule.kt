package jp.cordea.mackerelclient.di

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.UserDeleteConfirmEventDispatcher
import jp.cordea.mackerelclient.UserDeleteConfirmSource
import jp.cordea.mackerelclient.fragment.UserFragment

@Module
interface UserFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            UserFragmentBindModule::class
        ]
    )
    fun contributeUserFragment(): UserFragment
}

@Module
interface UserFragmentBindModule {
    @Binds
    fun bindFragment(fragment: UserFragment): Fragment

    @Binds
    fun bindUserDeleteConfirmSource(
        dispatcher: UserDeleteConfirmEventDispatcher
    ): UserDeleteConfirmSource
}
