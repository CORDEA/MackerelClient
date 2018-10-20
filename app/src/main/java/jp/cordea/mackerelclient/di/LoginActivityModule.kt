package jp.cordea.mackerelclient.di

import android.app.Activity
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.cordea.mackerelclient.activity.LoginActivity
import jp.cordea.mackerelclient.viewmodel.LoginViewModel

@Module
interface LoginActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            LoginActivityBindModule::class,
            LoginViewModelModule::class
        ]
    )
    fun contributeLoginActivity(): LoginActivity
}

@Module
interface LoginActivityBindModule {
    @Binds
    fun bindActivity(activity: LoginActivity): Activity

    @Binds
    fun bindViewModelStoreOwner(activity: LoginActivity): ViewModelStoreOwner
}

@Module
class LoginViewModelModule : ViewModelModule<LoginViewModel>(LoginViewModel::class)
