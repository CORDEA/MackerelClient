package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.CompletableSubject
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.CloseAlert
import jp.cordea.mackerelclient.model.DisplayableAlert
import jp.cordea.mackerelclient.utils.applyCommonProgressStyle
import javax.inject.Inject

class AlertCloseDialogFragment : DialogFragment() {

    @Inject
    lateinit var apiClient: MackerelApiClient

    private val onDismiss = CompletableSubject.create()

    private var throwable: Throwable? = null
    private var disposable: Disposable? = null

    private val alert by lazy { arguments!!.getParcelable<DisplayableAlert>(ALERT_KEY)!! }
    private val reason by lazy { arguments!!.getString(REASON_KEY)!! }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposable = apiClient.closeAlert(alert.id, CloseAlert(reason))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dismiss()
            }, {
                throwable = it
                dismiss()
            })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        return ProgressDialog(context)
            .applyCommonProgressStyle()
            .apply {
                setMessage(context.getString(R.string.progress_dialog_title))
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun dismiss() {
        super.dismiss()
        val throwable = throwable
        if (throwable == null) {
            onDismiss.onComplete()
        } else {
            onDismiss.onError(throwable)
        }
    }

    fun show(fragmentManager: FragmentManager): Completable {
        show(fragmentManager, TAG)
        return onDismiss
    }

    companion object {
        private const val TAG = "AlertCloseDialogFragment"
        private const val ALERT_KEY = "AlertKey"
        private const val REASON_KEY = "ReasonKey"

        fun newInstance(alert: DisplayableAlert, reason: String) =
            AlertCloseDialogFragment().apply {
                arguments = bundleOf(
                    ALERT_KEY to alert,
                    REASON_KEY to reason
                )
            }
    }
}
