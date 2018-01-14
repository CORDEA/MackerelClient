package jp.cordea.mackerelclient.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.*
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.api.response.RefreshMonitor
import jp.cordea.mackerelclient.utils.DialogUtils
import kotterknife.bindView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonitorEditActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val type: TextView by bindView(R.id.type)

    val name: EditText by bindView(R.id.name)

    val service: EditText by bindView(R.id.service)

    val duration: EditText by bindView(R.id.duration)

    val metric: EditText by bindView(R.id.metric)

    val operator: Spinner by bindView(R.id.operator)

    val operatorPair: Spinner by bindView(R.id.operator_pair)

    val warning: EditText by bindView(R.id.warning)

    val critical: EditText by bindView(R.id.critical)

    val notInterval: EditText by bindView(R.id.notification_interval)

    val scopes: EditText by bindView(R.id.scopes)

    val exScopes: EditText by bindView(R.id.exclude_scopes)

    val optionContainer: View by bindView(R.id.option_container)

    val done: Button by bindView(R.id.done)

    val discard: Button by bindView(R.id.discard)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor_edit)
        setSupportActionBar(toolbar)

        discard.setOnClickListener {
            finish()
        }

        val context: Context = this
        val monitor = intent.getSerializableExtra(MonitorKey) as Monitor

        initValues(monitor)

        operator.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                operatorPair.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        operatorPair.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                operator.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        done.setOnClickListener {
            val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
            dialog.show()

            val ref = checkValues(monitor)
            MackerelApiClient
                    .refreshMonitor(this, ref.id, ref)
                    .enqueue(object : Callback<RefreshMonitor> {
                        override fun onResponse(p0: Call<RefreshMonitor>?, response: Response<RefreshMonitor>?) {
                            dialog.dismiss()
                            response?.let {
                                if (it.isSuccessful) {
                                    finish()
                                } else {
                                    DialogUtils.switchDialog(context, it,
                                            R.string.monitor_refresh_error_dialog_title,
                                            R.string.error_403_dialog_message)
                                }
                                return
                            }
                            DialogUtils.showDialog(context,
                                    R.string.monitor_refresh_error_dialog_title)
                        }

                        override fun onFailure(p0: Call<RefreshMonitor>?, p1: Throwable?) {
                            dialog.dismiss()
                            DialogUtils.showDialog(context,
                                    R.string.monitor_refresh_error_dialog_title)
                        }
                    })
        }
    }

    private fun initValues(monitor: Monitor) {
        type.text = monitor.type
        if (monitor.type == "connectivity") {
            optionContainer.visibility = View.GONE
        } else {
            name.text = with(monitor.name ?: "", { SpannableStringBuilder(this) })
            service.text = with(monitor.service ?: "", { SpannableStringBuilder(this) })
            duration.text = with(monitor.duration
                    ?: "", { SpannableStringBuilder(this.toString()) })
            metric.text = with(monitor.metric ?: "", { SpannableStringBuilder(this) })
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayOf("<", ">"))
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            operator.adapter = adapter
            operatorPair.adapter = adapter
            operator.setSelection(if ("<" == monitor.operator) 0 else 1)
            operatorPair.setSelection(operator.selectedItemPosition)
            warning.text = with(monitor.warning ?: "", { SpannableStringBuilder(this.toString()) })
            critical.text = with(monitor.critical
                    ?: "", { SpannableStringBuilder(this.toString()) })
            notInterval.text = with(monitor.notificationInterval
                    ?: "", { SpannableStringBuilder(this.toString()) })
        }
        scopes.text = with(monitor.scopes.joinToString(", "), { SpannableStringBuilder(this) })
        exScopes.text = with(monitor.excludeScopes.joinToString(", "), { SpannableStringBuilder(this) })
    }

    private fun checkValues(monitor: Monitor): Monitor {

        val duration = try {
            checkValue(monitor.duration, duration.text.toString().toInt())
        } catch (_: NumberFormatException) {
            null
        }

        val warning = try {
            checkValue(monitor.warning, warning.text.toString().toFloat())
        } catch (_: NumberFormatException) {
            null
        }

        val critical = try {
            checkValue(monitor.critical, critical.text.toString().toFloat())
        } catch (_: NumberFormatException) {
            null
        }

        val interval = try {
            checkValue(monitor.notificationInterval, notInterval.text.toString().toInt())
        } catch (_: NumberFormatException) {
            null
        }

        return Monitor(
                monitor.id,
                monitor.type,
                checkValue(monitor.name, name.text.toString()),
                checkValue(monitor.service, service.text.toString()),
                duration,
                checkValue(monitor.metric, metric.text.toString()),
                checkValue(monitor.operator, operator.selectedItem as String),
                warning,
                critical,
                interval,
                monitor.url,
                scopes.text.toString().split(",").map(String::trim).toTypedArray(),
                exScopes.text.toString().split(",").map(String::trim).toTypedArray()
        )
    }

    private fun <T> checkValue(v0: T, v1: T): T? {
        if (v0 != v1) {
            return v1
        }
        return null
    }

    companion object {

        private const val MonitorKey = "MonitorKey"

        fun createIntent(context: Context, monitor: Monitor): Intent =
                Intent(context, MonitorEditActivity::class.java).apply {
                    putExtra(MonitorKey, monitor)
                }
    }
}
