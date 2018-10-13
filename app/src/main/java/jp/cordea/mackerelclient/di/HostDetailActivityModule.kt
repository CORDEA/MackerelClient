package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.HostDetailActivity

@Module
interface HostDetailActivityModule {
    @ContributesAndroidInjector
    fun contributeHostDetailActivity(): HostDetailActivity
}
