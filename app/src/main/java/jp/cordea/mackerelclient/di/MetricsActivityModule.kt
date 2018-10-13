package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.MetricsActivity

@Module
interface MetricsActivityModule {
    @ContributesAndroidInjector
    fun contributeMetricsActivity(): MetricsActivity
}
