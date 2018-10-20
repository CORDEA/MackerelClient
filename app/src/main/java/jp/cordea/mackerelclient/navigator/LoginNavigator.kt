package jp.cordea.mackerelclient.navigator

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.MainActivity
import jp.cordea.mackerelclient.di.ActivityScope
import javax.inject.Inject

@ActivityScope
class LoginNavigator @Inject constructor(
    private val activity: Activity
) {
    fun navigateToMain() {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
    }

    fun showKeyRequiredErrorDialog() {
        AlertDialog
            .Builder(activity)
            .setTitle(R.string.sign_in_error_dialog_title)
            .setMessage(R.string.sign_in_error_dialog_message_key)
            .show()
    }

    fun showWrongEmailErrorDialog() {
        AlertDialog
            .Builder(activity)
            .setTitle(R.string.sign_in_error_dialog_title)
            .setMessage(R.string.sign_in_error_dialog_message_mail)
            .show()
    }

    fun showErrorDialog() {
        AlertDialog
            .Builder(activity)
            .setTitle(R.string.sign_in_error_dialog_title)
            .setMessage(R.string.sign_in_error_dialog_message_other)
            .show()
    }
}
