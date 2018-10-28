package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import dagger.android.support.AndroidSupportInjection
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.model.DisplayableAlert
import javax.inject.Inject

class AlertCloseConfirmDialogFragment : DialogFragment() {
    @Inject
    lateinit var listener: AlertCloseDialogListener

    private val alert by lazy { arguments!!.getParcelable<DisplayableAlert>(ALERT_KEY)!! }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null)
        val editText = layout.findViewById(R.id.reason) as EditText
        return AlertDialog.Builder(context)
            .setTitle(R.string.alert_detail_close_dialog_title)
            .setView(layout)
            .setPositiveButton(R.string.alert_detail_close_positive_button) { _, _ ->
                listener.onNext(AlertCloseConfirmResult.Confirm(alert, editText.text.toString()))
                dismiss()
            }
            .create()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    companion object {
        private const val TAG = "AlertCloseConfirmDialogFragment"
        private const val ALERT_KEY = "AlertKey"

        fun newInstance(alert: DisplayableAlert) =
            AlertCloseConfirmDialogFragment().apply {
                arguments = bundleOf(ALERT_KEY to alert)
            }
    }
}
