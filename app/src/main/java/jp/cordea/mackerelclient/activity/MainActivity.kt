package jp.cordea.mackerelclient.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ImageView
import com.squareup.picasso.Picasso
import io.realm.Realm
import jp.cordea.mackerelclient.PicassoCircularTransform
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ActivityMainBinding
import jp.cordea.mackerelclient.databinding.NavHeaderMainBinding
import jp.cordea.mackerelclient.fragment.HostFragment
import jp.cordea.mackerelclient.fragment.MonitorFragment
import jp.cordea.mackerelclient.fragment.ServiceFragment
import jp.cordea.mackerelclient.fragment.SettingFragment
import jp.cordea.mackerelclient.fragment.UserFragment
import jp.cordea.mackerelclient.fragment.alert.AlertFragment
import jp.cordea.mackerelclient.model.Preferences
import jp.cordea.mackerelclient.model.UserKey
import jp.cordea.mackerelclient.utils.GravatarUtils

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private val prefs by lazy {
        Preferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
        val header = binding.navView.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(header)

        val userId = prefs.userId
        val realm = Realm.getDefaultInstance()
        var user: UserKey? = null
        realm.where(UserKey::class.java).equalTo("id", userId).findFirst()?.let {
            user = realm.copyFromRealm(it)
        }
        realm.close()

        if (user == null) {
            AlertDialog
                .Builder(this)
                .setTitle(R.string.nav_bar_sign_out_dialog_title)
                .setPositiveButton(R.string.nav_bar_sign_out_dialog_positive_button) { _, _ ->
                    val intent = Intent(this as Context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .show()
        } else {
            val u = user!!
            if (!u.email.isNullOrBlank() && !u.name.isNullOrBlank()) {
                val thumbnail: ImageView =
                    header.findViewById(R.id.user_thumbnail_image_view) as ImageView
                headerBinding.nameTextView.text = u.name
                headerBinding.emailTextView.text = u.email
                GravatarUtils.getGravatarImage(
                    u.email!!,
                    resources.getDimensionPixelSize(R.dimen.user_thumbnail_size)
                )?.let {
                    Picasso.with(this)
                        .load(it)
                        .transform(PicassoCircularTransform())
                        .into(thumbnail)
                }
            } else {
                headerBinding.nameTextView.text = resources.getString(R.string.anonymous)
                headerBinding.emailTextView.text = ""
            }
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, AlertFragment.newInstance())
            .commit()
    }

    override fun onResume() {
        super.onResume()

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.appbar.elevation = 0f
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val context: Context = this
        val id = item.itemId

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.appbar.elevation = if (id == R.id.nav_alert ||
                id == R.id.nav_sign_out ||
                id == R.id.nav_open_mackerel
            ) {
                0f
            } else {
                resources.getDimension(R.dimen.app_bar_elevation)
            }
        }

        val transaction = supportFragmentManager.beginTransaction()
        val fragment: Fragment
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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MACKEREL_URL))
                startActivity(intent)
            }
            R.id.nav_sign_out -> {
                signOut(context)
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOut(context: Context) {
        AlertDialog
            .Builder(context)
            .setTitle(R.string.nav_bar_sign_out_dialog_title)
            .setPositiveButton(R.string.nav_bar_sign_out_dialog_positive_button) { _, _ ->
                val userId = prefs.userId
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    it.where(UserKey::class.java)
                        .equalTo("id", userId)
                        .findFirst()
                        ?.deleteFromRealm()
                }
                realm.close()

                prefs.clear()

                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
            .show()
    }

    companion object {

        private const val MACKEREL_URL = "https://mackerel.io"
    }
}
