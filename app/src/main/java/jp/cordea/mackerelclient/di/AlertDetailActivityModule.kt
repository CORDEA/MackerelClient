package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.AlertDetailActivity

@Module
interface AlertDetailActivityModule {
    @ContributesAndroidInjector
    fun contributeAlertDetailActivity(): AlertDetailActivity
}
