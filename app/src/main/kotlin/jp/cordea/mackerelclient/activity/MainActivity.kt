package jp.cordea.mackerelclient.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.squareup.picasso.Picasso
import io.realm.Realm
import jp.cordea.mackerelclient.PicassoCircularTransform
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.fragment.*
import jp.cordea.mackerelclient.fragment.alert.AlertFragment
import jp.cordea.mackerelclient.model.UserKey
import jp.cordea.mackerelclient.utils.GravatarUtils
import jp.cordea.mackerelclient.utils.PreferenceUtils

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private val MackerelUrl = "https://mackerel.io"
    }

    val appbar: AppBarLayout by bindView(R.id.appbar)
    val toolbar: Toolbar by bindView(R.id.toolbar)
    val drawer: DrawerLayout by bindView(R.id.drawer_layout)
    val navigationView: NavigationView by bindView(R.id.nav_view)

    private var elevation: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
        val header = navigationView.getHeaderView(0)
        val name: TextView = header.findViewById(R.id.name) as TextView
        val email: TextView = header.findViewById(R.id.email) as TextView

        val userId = PreferenceUtils.readUserId(applicationContext)
        val realm = Realm.getInstance(applicationContext)
        var user: UserKey? = null
        realm.where(UserKey::class.java).equalTo("id", userId).findFirst()?.let {
            user = realm.copyFromRealm(it)
        }
        realm.close()

        if (user == null) {
            AlertDialog
                    .Builder(this)
                    .setTitle(R.string.nav_bar_sign_out_dialog_title)
                    .setPositiveButton(R.string.nav_bar_sign_out_dialog_positive_button, { dialogInterface, i ->
                        val intent = Intent(this as Context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
                    .show()
        } else {
            val u = user!!
            if (!u.email.isNullOrBlank() && !u.name.isNullOrBlank()) {
                val thumbnail: ImageView = header.findViewById(R.id.user_thumbnail) as ImageView
                name.text = u.name
                email.text = u.email
                GravatarUtils.getGravatarImage(u.email!!,
                        applicationContext.resources.getDimensionPixelSize(R.dimen.user_thumbnail_size))?.let {
                    Picasso.with(applicationContext)
                            .load(it)
                            .transform(PicassoCircularTransform())
                            .into(thumbnail)
                }
            } else {
                name.text = resources.getString(R.string.anonymous)
                email.text = ""
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.container, AlertFragment.newInstance()).commit()
    }

    override fun onStart() {
        super.onResume()

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = appbar.elevation
            appbar.elevation = -1f
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val context: Context = this
        val id = item.itemId

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation?.let {
                appbar.elevation = if (id == R.id.nav_alert || id == R.id.nav_sign_out || id == R.id.nav_open_mackerel) -1f else it
            }
        }

        val transaction = supportFragmentManager.beginTransaction()
        var fragment: android.support.v4.app.Fragment
        when (id) {
            R.id.nav_alert -> {
                fragment = AlertFragment.newInstance()
                transaction.replace(R.id.container, fragment).commit()
            }
            R.id.nav_host -> {
                fragment = HostFragment.newInstance()
                transaction.replace(R.id.container, fragment).commit()
            }
            R.id.nav_service -> {
                fragment = ServiceFragment.newInstance()
                transaction.replace(R.id.container, fragment).commit()
            }
            R.id.nav_monitor -> {
                fragment = MonitorFragment.newInstance()
                transaction.replace(R.id.container, fragment).commit()
            }
            R.id.nav_user -> {
                fragment = UserFragment.newInstance()
                transaction.replace(R.id.container, fragment).commit()
            }
            R.id.nav_setting -> {
                fragment = SettingFragment.newInstance()
                transaction.replace(R.id.container, fragment).commit()
            }
            R.id.nav_open_mackerel -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MackerelUrl))
                startActivity(intent)
            }
            R.id.nav_sign_out -> {
                signOut(context)
            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOut(context: Context) {
        AlertDialog
                .Builder(context)
                .setTitle(R.string.nav_bar_sign_out_dialog_title)
                .setPositiveButton(R.string.nav_bar_sign_out_dialog_positive_button, { dialogInterface, i ->
                    val userId = PreferenceUtils.readUserId(context)
                    val realm = Realm.getInstance(context)
                    realm.executeTransaction {
                        it.where(UserKey::class.java).equalTo("id", userId).findFirst()?.let {
                            it.removeFromRealm()
                        }
                    }
                    realm.close()

                    PreferenceUtils.removeUserId(context)

                    val intent = Intent(context, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                })
                .show()
    }
}
