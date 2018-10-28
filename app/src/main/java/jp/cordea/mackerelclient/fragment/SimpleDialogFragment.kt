package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

class SimpleDialogFragment : DialogFragment() {
    private val title get() = arguments!!.getInt(TITLE_KEY, 0)
    private val message get() = arguments!!.getInt(MESSAGE_KEY, 0)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(context!!)
            .apply {
                if (message == 0) {
                    setMessage(title)
                } else {
                    setTitle(title)
                    setMessage(message)
                }
            }
            .create()

    companion object {
        private const val TITLE_KEY = "TitleKey"
        private const val MESSAGE_KEY = "MessageKey"

        fun newInstance(@StringRes title: Int, @StringRes message: Int = 0) =
            SimpleDialogFragment().apply {
                arguments = bundleOf(
                    TITLE_KEY to title,
                    MESSAGE_KEY to message
                )
            }
    }
}
