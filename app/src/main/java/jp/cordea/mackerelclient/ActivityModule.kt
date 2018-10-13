package jp.cordea.mackerelclient

import dagger.Module
import jp.cordea.mackerelclient.di.AlertDetailActivityModule
import jp.cordea.mackerelclient.di.HostDetailActivityModule
import jp.cordea.mackerelclient.di.LicenseActivityModule
import jp.cordea.mackerelclient.di.LoginActivityModule
import jp.cordea.mackerelclient.di.MainActivityModule
import jp.cordea.mackerelclient.di.MetricsActivityModule
import jp.cordea.mackerelclient.di.MetricsEditActivityModule
import jp.cordea.mackerelclient.di.MonitorDetailActivityModule
import jp.cordea.mackerelclient.di.ServiceMetricsActivityModule

@Module(
    includes = [
        AlertDetailActivityModule::class,
        HostDetailActivityModule::class,
        LicenseActivityModule::class,
        LoginActivityModule::class,
        MainActivityModule::class,
        MetricsActivityModule::class,
        MetricsEditActivityModule::class,
        MonitorDetailActivityModule::class,
        ServiceMetricsActivityModule::class
    ]
)
interface ActivityModule
