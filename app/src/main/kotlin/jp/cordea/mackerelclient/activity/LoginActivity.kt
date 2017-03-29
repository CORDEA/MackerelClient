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
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.model.Preferences
import jp.cordea.mackerelclient.model.UserKey
import jp.cordea.mackerelclient.viewmodel.LoginViewModel
import rx.subscriptions.CompositeSubscription

class LoginActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val progress: ProgressBar by bindView(R.id.progress)

    val container: View by bindView(R.id.container)

    val button: Button by bindView(R.id.button)

    val apiKey: EditText by bindView(R.id.api_key)

    val email: EditText by bindView(R.id.email)

    private val viewModel by lazy {
        LoginViewModel(this)
    }

    private val prefs by lazy {
        Preferences(this)
    }

    private val compositeSubscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        var userKey: UserKey? = null
        val userId = prefs.userId
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

            compositeSubscription.add(
                    viewModel.logIn(it0.key!!, it0.email, true,
                            onSuccess = {
                                onLoginSucceeded(it)
                            },
                            onFailure = {
                                onLoginFailure()
                            }
                    )
            )
        }

        setEvents()
    }

    private fun setEvents() {
        apiKey.setOnEditorActionListener({ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                email.requestFocus()
            }
            true
        })

        email.setOnEditorActionListener({ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                im?.hideSoftInputFromWindow(this.currentFocus.windowToken, 0)
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
                compositeSubscription.add(
                        viewModel.logIn(t.toString().trim(), email.text.toString(), false,
                                onSuccess = {
                                    onLoginSucceeded(it)
                                },
                                onFailure = {
                                    onLoginFailure()
                                }
                        )
                )
            }
        }
    }

    private fun onLoginSucceeded(id: Int? = null) {
        id?.let {
            prefs.userId = it
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun onLoginFailure() {
        AlertDialog
                .Builder(this)
                .setTitle(R.string.sign_in_error_dialog_title)
                .setMessage(R.string.sign_in_error_dialog_message_mail)
                .show()
        container.visibility = View.VISIBLE
        progress.visibility = View.GONE
    }

    override fun onDestroy() {
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe()
        }
        super.onDestroy()
    }
}
