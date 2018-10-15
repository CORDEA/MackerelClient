package jp.cordea.mackerelclient.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Module
import dagger.Provides
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule<T : ViewModel>(
    private val kClass: KClass<T>
) {
    @Provides
    fun provideViewModel(
        owner: ViewModelStoreOwner,
        factory: ViewModelFactory<T>
    ): T {
        if (owner is Fragment) {
            return ViewModelProviders.of(owner, factory).get(kClass.java)
        }
        if (owner is FragmentActivity) {
            return ViewModelProviders.of(owner, factory).get(kClass.java)
        }
        throw IllegalArgumentException()
    }
}
