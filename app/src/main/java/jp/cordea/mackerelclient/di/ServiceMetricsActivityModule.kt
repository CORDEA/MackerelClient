package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.ServiceMetricsActivity

@Module
interface ServiceMetricsActivityModule {
    @ActivityScope
    @ContributesAndroidInjector
    fun contributeServiceMetricsActivity(): ServiceMetricsActivity
}
