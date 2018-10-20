package jp.cordea.mackerelclient.activity

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ActivityLoginBinding
import jp.cordea.mackerelclient.databinding.ContentLoginBinding
import jp.cordea.mackerelclient.viewmodel.LoginViewModel
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: LoginViewModel

    private val compositeDisposable = CompositeDisposable()

    private lateinit var contentBinding: ContentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        setSupportActionBar(binding.toolbar)

        contentBinding = binding.content

        viewModel.email
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { contentBinding.emailEditText.text = it }
            .addTo(compositeDisposable)

        viewModel.apiKey
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { contentBinding.apiKeyEditText.text = it }
            .addTo(compositeDisposable)

        viewModel.isProgressBarVisible
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { contentBinding.progressBar.isVisible = it }
            .addTo(compositeDisposable)

        viewModel.isContainerVisible
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { contentBinding.container.isVisible = it }
            .addTo(compositeDisposable)

        viewModel.autoLogin()
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
            viewModel.clickedButton(
                contentBinding.apiKeyEditText.text.toString(),
                contentBinding.emailEditText.text.toString()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
