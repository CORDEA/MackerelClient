package jp.cordea.mackerelclient

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.realm.Realm
import io.realm.RealmConfiguration
import jp.cordea.mackerelclient.model.DisplayHostState
import javax.inject.Inject

class McApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_VERSION)
                .migration { dynamicRealm, old, _ ->
                    val scheme = dynamicRealm.schema
                    var oldVersion = old
                    if (oldVersion == 0L) {
                        scheme[DisplayHostState::class.java.simpleName]!!
                            .addField("new_name", String::class.java)
                            .transform {
                                it.setString("new_name", it.getString("name"))
                            }
                            .removeField("name")
                            .addPrimaryKey("new_name")
                            .renameField("new_name", "name")
                        ++oldVersion
                    }
                    if (oldVersion == 1L) {
                        scheme[DisplayHostState::class.java.simpleName]!!
                            .removePrimaryKey()
                            .setRequired(DisplayHostState::name.name, true)
                            .addIndex(DisplayHostState::name.name)
                            .addPrimaryKey(DisplayHostState::name.name)
                        ++oldVersion
                    }
                }.build()
        )

        DaggerAppComponent
            .builder()
            .application(this)
            .build()
            .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    companion object {
        private const val SCHEMA_VERSION = 2L
    }
}
