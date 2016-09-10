package jp.cordea.mackerelclient

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by CORDEA on 2016/09/10.
 */
class MCApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.setDefaultConfiguration(RealmConfiguration.Builder(this).build())
    }

}