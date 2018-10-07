package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import io.realm.Realm
import io.realm.RealmResults
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.utils.StatusUtils

class SettingStatusSelectionDialogFragment : DialogFragment() {

    interface OnUpdateStatusListener {
        fun onUpdateStatus()
    }

    private val realm = Realm.getDefaultInstance()

    private var lastItem = 0

    private lateinit var listener: OnUpdateStatusListener
    private lateinit var items: RealmResults<DisplayHostState>

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = parentFragment as OnUpdateStatusListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        items = realm.where(DisplayHostState::class.java).findAll()
        return AlertDialog.Builder(context)
                .setMultiChoiceItems(
                        items.map { StatusUtils.requestNameToString(it.name) }.toTypedArray(),
                        BooleanArray(items.size) { i -> items[i]!!.isDisplay!! }
                ) { _, which, flag ->
                    val item = items[which]!!
                    realm.executeTransaction {
                        item.isDisplay = flag
                    }
                    lastItem = which
                    listener.onUpdateStatus()
                }.show()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        if (items.any { it.isDisplay!! }) {
            return
        }
        AlertDialog
                .Builder(context!!)
                .setMessage(R.string.setting_status_select_limit_dialog_message)
                .show()
        val item = items.first { it.name == items[lastItem]!!.name }
        realm.executeTransaction {
            item.isDisplay = true
        }
        listener.onUpdateStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    companion object {
        const val TAG = "SettingStatusSelectionDialogFragment"
    }
}
