package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import io.realm.Realm
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.model.MetricsParameter
import jp.cordea.mackerelclient.model.UserMetric

class MetricsDeleteConfirmDialogFragment : DialogFragment() {

    interface OnDeleteMetricsListener {
        fun onDelete(position: Int)
    }

    private lateinit var listener: OnDeleteMetricsListener

    private val realm = Realm.getDefaultInstance()
    private val metricsId get() = arguments!!.getInt(ID_KEY)
    private val position get() = arguments!!.getInt(POSITION_KEY)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = activity as OnDeleteMetricsListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog
            .Builder(context!!)
            .setMessage(R.string.metrics_card_delete_dialog_title)
            .setPositiveButton(R.string.button_positive) { _, _ ->
                realm.executeTransaction {
                    realm.where(UserMetric::class.java)
                        .equalTo("id", metricsId)
                        .findFirst()!!
                        .deleteFromRealm()
                }
                listener.onDelete(position)
            }.create()

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    companion object {
        const val TAG = "MetricsDeleteConfirmDialogFragment"

        private const val ID_KEY = "IdKey"
        private const val POSITION_KEY = "PositionKey"

        fun newInstance(items: List<MetricsParameter>, position: Int) =
            MetricsDeleteConfirmDialogFragment().apply {
                arguments = bundleOf(
                    ID_KEY to items[position].id,
                    POSITION_KEY to position
                )
            }
    }
}
