package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import butterknife.bindView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.api.response.CloseAlert
import jp.cordea.mackerelclient.utils.DateUtils
import jp.cordea.mackerelclient.utils.DialogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertDetailActivity : AppCompatActivity() {
    companion object {
        public val AlertKey = "AlertKey"
    }

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    private var alert: Alert? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_common)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val alert = intent.getParcelableExtra<Alert>(AlertKey)

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = DetailCommonAdapter(applicationContext, insertInfo(alert))

        this.alert = alert
    }

    private fun insertInfo(alert: Alert): List<List<Pair<String, Int>>> {
        val list: MutableList<MutableList<Pair<String, Int>>> = arrayListOf()
        var inner: MutableList<Pair<String, Int>> = arrayListOf()
        inner.add(Pair(alert.status!!, R.string.alert_detail_status))
        inner.add(Pair(DateUtils.stringDateFromEpoch(alert.openedAt!!), R.string.alert_detail_opened_at))
        alert.closedAt?.let {
            inner.add(Pair(DateUtils.stringDateFromEpoch(it), R.string.alert_detail_closed_at))
        }
        list.add(inner)

        inner = arrayListOf()
        alert.reason?.let {
            inner.add(Pair(it, R.string.alert_detail_reason))
        }
        inner.add(Pair(alert.type!!, R.string.alert_detail_type))
        alert.value?.let {
            inner.add(Pair(it.toString(), R.string.alert_detail_value))
        }
        alert.message?.let {
            inner.add(Pair(it, R.string.alert_detail_message))
        }
        list.add(inner)
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.alert_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val context = this
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_close -> {
                val layout = layoutInflater.inflate(R.layout.dialog_edit_text, null)
                val editText: EditText = layout.findViewById(R.id.reason) as EditText

                AlertDialog.Builder(context)
                        .setTitle(R.string.alert_detail_close_dialog_title)
                        .setView(layout)
                        .setPositiveButton(R.string.alert_detail_close_positive_button, { d, w ->
                            val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                            dialog.show()
                            MackerelApiClient
                                    .closeAlert(context, alert!!.id!!, CloseAlert(editText.text.toString()))
                                    .enqueue(object : Callback<Alert> {
                                        override fun onResponse(p0: Call<Alert>?, response: Response<Alert>?) {
                                            dialog.dismiss()
                                            response?.let {
                                                val success = DialogUtils.switchDialog(context, it,
                                                        R.string.alert_detail_error_close_dialog_title,
                                                        R.string.error_403_dialog_message)
                                                if (success) {
                                                    setResult(Activity.RESULT_OK)
                                                    finish()
                                                }
                                                return
                                            }
                                            DialogUtils.showDialog(context,
                                                    R.string.alert_detail_error_close_dialog_title)
                                        }

                                        override fun onFailure(p0: Call<Alert>?, p1: Throwable?) {
                                            dialog.dismiss()
                                            DialogUtils.showDialog(context,
                                                    R.string.alert_detail_error_close_dialog_title)
                                        }
                                    })
                        })
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
