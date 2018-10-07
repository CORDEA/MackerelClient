package jp.cordea.mackerelclient.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.realm.Realm
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ActivityLoginBinding
import jp.cordea.mackerelclient.databinding.ContentLoginBinding
import jp.cordea.mackerelclient.model.Preferences
import jp.cordea.mackerelclient.model.UserKey
import jp.cordea.mackerelclient.viewmodel.LoginViewModel
import rx.subscriptions.CompositeSubscription

class LoginActivity : AppCompatActivity() {

    private val viewModel by lazy {
        LoginViewModel(this)
    }

    private val prefs by lazy {
        Preferences(this)
    }

    private val compositeSubscription = CompositeSubscription()

    private lateinit var contentBinding: ContentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        setSupportActionBar(binding.toolbar)

        contentBinding = binding.content

        var userKey: UserKey? = null
        val userId = prefs.userId
        val realm = Realm.getDefaultInstance()
        realm.where(UserKey::class.java).equalTo("id", userId).findFirst()?.let {
            userKey = realm.copyFromRealm(it)
        }
        realm.close()

        userKey?.let { key ->
            contentBinding.progressBar.visibility = View.VISIBLE
            contentBinding.container.visibility = View.GONE
            contentBinding.apiKeyEditText.text = with(key.key) { SpannableStringBuilder(this) }
            key.email?.let {
                contentBinding.emailEditText.text = with(it) { SpannableStringBuilder(this) }
            }

            compositeSubscription.add(
                viewModel.logIn(key.key!!, key.email, true,
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
        contentBinding.apiKeyEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                contentBinding.emailEditText.requestFocus()
            }
            true
        }

        contentBinding.emailEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                im?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
            true
        }

        contentBinding.button.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val apiKey = contentBinding.apiKeyEditText.text
        if (apiKey.isEmpty()) {
            AlertDialog
                .Builder(this)
                .setTitle(R.string.sign_in_error_dialog_title)
                .setMessage(R.string.sign_in_error_dialog_message_key)
                .show()
        } else {
            contentBinding.progressBar.visibility = View.VISIBLE
            contentBinding.container.visibility = View.GONE
            compositeSubscription.add(
                viewModel.logIn(
                    apiKey.toString().trim(),
                    contentBinding.emailEditText.text.toString(),
                    false,
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
        contentBinding.container.visibility = View.VISIBLE
        contentBinding.progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe()
        }
        super.onDestroy()
    }
}
