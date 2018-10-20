package jp.cordea.mackerelclient.viewmodel

import android.text.SpannableStringBuilder
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import jp.cordea.mackerelclient.model.Preferences
import jp.cordea.mackerelclient.navigator.LoginNavigator
import jp.cordea.mackerelclient.repository.LoginRepository
import jp.cordea.mackerelclient.repository.WrongEmailException
import javax.inject.Inject

class LoginViewModel : ViewModel() {
    @Inject
    lateinit var repository: LoginRepository

    @Inject
    lateinit var navigator: LoginNavigator

    @Inject
    lateinit var preferences: Preferences

    private val compositeDisposable = CompositeDisposable()

    val isProgressBarVisible = PublishSubject.create<Boolean>()
    val isContainerVisible = PublishSubject.create<Boolean>()
    val apiKey = PublishSubject.create<SpannableStringBuilder>()
    val email = PublishSubject.create<SpannableStringBuilder>()

    fun clickedButton(
        apiKey: String,
        email: String
    ) {
        if (apiKey.isBlank()) {
            navigator.showKeyRequiredErrorDialog()
        } else {
            isProgressBarVisible.onNext(true)
            isContainerVisible.onNext(false)
            repository.login(apiKey.trim(), email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    preferences.userId = it
                    navigator.navigateToMain()
                }, {
                    isContainerVisible.onNext(true)
                    isProgressBarVisible.onNext(false)
                    if (it is WrongEmailException) {
                        navigator.showWrongEmailErrorDialog()
                    } else {
                        navigator.showErrorDialog()
                    }
                })
                .addTo(compositeDisposable)
        }
    }

    fun autoLogin() {
        val key = repository.getLoginUser(preferences.userId) ?: return
        isProgressBarVisible.onNext(true)
        isContainerVisible.onNext(false)
        apiKey.onNext(SpannableStringBuilder(key.key))
        key.email?.let { email.onNext(SpannableStringBuilder(it)) }
        repository.autoLogin(key.key)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                navigator.navigateToMain()
            }, {
                isContainerVisible.onNext(true)
                isProgressBarVisible.onNext(false)
                if (it is WrongEmailException) {
                    navigator.showWrongEmailErrorDialog()
                } else {
                    navigator.showErrorDialog()
                }
            })
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
