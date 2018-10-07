package jp.cordea.mackerelclient

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import jp.cordea.mackerelclient.model.DisplayHostState

class McApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
            .schemaVersion(SCHEMA_VERSION)
            .migration { dynamicRealm, old, _ ->
                val scheme = dynamicRealm.schema
                var oldVersion = old
                if (old == 0L) {
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
            }.build()
        )
    }

    companion object {
        private const val SCHEMA_VERSION = 1L
    }
}