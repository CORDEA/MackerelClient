package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.LoginActivity

@Module
interface LoginActivityModule {
    @ActivityScope
    @ContributesAndroidInjector
    fun contributeLoginActivity(): LoginActivity
}
