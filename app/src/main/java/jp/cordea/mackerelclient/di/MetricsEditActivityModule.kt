package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.MetricsEditActivity

@Module
interface MetricsEditActivityModule {
    @ActivityScope
    @ContributesAndroidInjector
    fun contributeMetricsEditActivity(): MetricsEditActivity
}
