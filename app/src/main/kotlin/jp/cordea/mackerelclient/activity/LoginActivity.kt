package jp.cordea.mackerelclient.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.SpannableStringBuilder
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import butterknife.bindView
import io.realm.Realm
import io.realm.RealmConfiguration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Users
import jp.cordea.mackerelclient.model.UserKey
import jp.cordea.mackerelclient.utils.PreferenceUtils
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class LoginActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val progress: ProgressBar by bindView(R.id.progress)
    val container: View by bindView(R.id.container)

    val button: Button by bindView(R.id.button)
    val apiKey: EditText by bindView(R.id.api_key)
    val email: EditText by bindView(R.id.email)

    private val compositeSubscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        var userKey: UserKey? = null
        val userId = PreferenceUtils.readUserId(applicationContext)
        val realm = Realm.getDefaultInstance()
        realm.where(UserKey::class.java).equalTo("id", userId).findFirst()?.let {
            userKey = realm.copyFromRealm(it)
        }
        realm.close()

        userKey?.let { it0 ->
            progress.visibility = View.VISIBLE
            container.visibility = View.GONE
            apiKey.text = with(it0.key, { SpannableStringBuilder(this) })
            it0.email?.let {
                email.text = with(it, { SpannableStringBuilder(this) })
            }

            compositeSubscription.add(apiRequest(it0.key!!, it0.email, true))
        }

        setEvents()
    }

    private fun setEvents() {
        apiKey.setOnEditorActionListener({ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                email.requestFocus()
            }
            true
        })

        email.setOnEditorActionListener({ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                im?.let {
                    it.hideSoftInputFromWindow(this.currentFocus.windowToken, 0)
                }
            }
            true
        })

        val context: Context = this
        button.setOnClickListener {
            val t = apiKey.text
            if (t.isEmpty()) {
                AlertDialog
                        .Builder(context)
                        .setTitle(R.string.sign_in_error_dialog_title)
                        .setMessage(R.string.sign_in_error_dialog_message_key)
                        .show()
            } else {
                progress.visibility = View.VISIBLE
                container.visibility = View.GONE
                compositeSubscription.add(apiRequest(t.toString().trim(), email.text.toString(), false))
            }
        }
    }

    private fun apiRequest(key: String, email: String?, autoLogin: Boolean): Subscription {
        val context: Context = this
        return MackerelApiClient
                .getUsers(context, key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (autoLogin) {
                        onSignInSucceed(context)
                    } else {
                        signIn(context, it, key, email)
                    }
                }, {
                    it.printStackTrace()
                    AlertDialog
                            .Builder(context)
                            .setMessage(R.string.sign_in_error_dialog_title)
                            .show()
                    container.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                })
    }

    private fun signIn(context: Context, it: Users, key: String, email: String?) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val maxId: Number? = realm.where(UserKey::class.java).max("id")
        val user = UserKey()
        val id = (maxId?.toInt() ?: -1) + 1
        user.id = id
        user.key = key

        if (email.isNullOrBlank()) {
            realm.copyToRealm(user)
            realm.commitTransaction()
            realm.close()
            onSignInSucceed(context, id)
        } else {
            val response = it.users.filter { it.email.equals(email) }
            if (response.size == 0) {
                realm.cancelTransaction()
                realm.close()
                AlertDialog
                        .Builder(context)
                        .setTitle(R.string.sign_in_error_dialog_title)
                        .setMessage(R.string.sign_in_error_dialog_message_mail)
                        .show()
                container.visibility = View.VISIBLE
                progress.visibility = View.GONE
            } else {
                user.email = response.first().email
                user.name = response.first().screenName
                realm.copyToRealm(user)
                realm.commitTransaction()
                realm.close()
                onSignInSucceed(context, id)
            }
        }
    }

    private fun onSignInSucceed(context: Context, id: Int? = null) {
        id?.let {
            PreferenceUtils.writeUserId(applicationContext, it)
        }
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe()
        }
        super.onDestroy()
    }
}
