package jp.cordea.mackerelclient.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.LicenseActivity

@Module
interface LicenseActivityModule {
    @ActivityScope
    @ContributesAndroidInjector
    fun contributeLicenseActivity(): LicenseActivity
}
