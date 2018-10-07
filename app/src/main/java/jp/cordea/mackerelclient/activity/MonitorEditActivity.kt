package jp.cordea.mackerelclient.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.api.response.RefreshMonitor
import jp.cordea.mackerelclient.databinding.ActivityMonitorEditBinding
import jp.cordea.mackerelclient.databinding.ContentMonitorEditBinding
import jp.cordea.mackerelclient.utils.DialogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonitorEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonitorEditBinding

    private lateinit var contentBinding: ContentMonitorEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor_edit)
        setSupportActionBar(binding.toolbar)

        binding.discardButton.setOnClickListener {
            finish()
        }

        contentBinding = binding.content
        val monitor = intent.getSerializableExtra(MONITOR_KEY) as Monitor

        initValues(monitor)

        contentBinding.operatorSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {
                        contentBinding.operatorPairSpinner.setSelection(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

        contentBinding.operatorPairSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {
                        contentBinding.operatorSpinner.setSelection(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

        binding.doneButton.setOnClickListener {
            refreshMonitor(monitor)
        }
    }

    private fun refreshMonitor(monitor: Monitor) {
        val context = this
        val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
        dialog.show()
        val ref = checkValues(monitor)
        MackerelApiClient
                .refreshMonitor(this, ref.id, ref)
                .enqueue(object : Callback<RefreshMonitor> {
                    override fun onResponse(
                            call: Call<RefreshMonitor>?,
                            response: Response<RefreshMonitor>?
                    ) {
                        dialog.dismiss()
                        response?.let {
                            if (it.isSuccessful) {
                                finish()
                            } else {
                                DialogUtils.switchDialog(
                                        context,
                                        it,
                                        R.string.monitor_refresh_error_dialog_title,
                                        R.string.error_403_dialog_message
                                )
                            }
                            return
                        }
                        DialogUtils.showDialog(
                                context,
                                R.string.monitor_refresh_error_dialog_title
                        )
                    }

                    override fun onFailure(p0: Call<RefreshMonitor>?, p1: Throwable?) {
                        dialog.dismiss()
                        DialogUtils.showDialog(
                                context,
                                R.string.monitor_refresh_error_dialog_title
                        )
                    }
                })
    }

    private fun initValues(monitor: Monitor) {
        contentBinding.typeTextView.text = monitor.type
        if (monitor.type == "connectivity") {
            contentBinding.optionContainer.visibility = View.GONE
        } else {
            contentBinding.nameEditText.text = with(
                    monitor.name ?: ""
            ) { SpannableStringBuilder(this) }
            contentBinding.serviceEditText.text = with(
                    monitor.service ?: ""
            ) { SpannableStringBuilder(this) }
            contentBinding.durationEditText.text = with(
                    monitor.duration ?: ""
            ) { SpannableStringBuilder(this.toString()) }
            contentBinding.metricEditText.text = with(
                    monitor.metric ?: ""
            ) { SpannableStringBuilder(this) }

            val adapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_item,
                    arrayOf("<", ">")
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            contentBinding.operatorSpinner.adapter = adapter
            contentBinding.operatorPairSpinner.adapter = adapter
            contentBinding.operatorSpinner.setSelection(
                    if ("<" == monitor.operator) 0 else 1
            )
            contentBinding.operatorPairSpinner.setSelection(
                    contentBinding.operatorSpinner.selectedItemPosition
            )
            contentBinding.warningEditText.text = with(
                    monitor.warning ?: ""
            ) { SpannableStringBuilder(this.toString()) }
            contentBinding.criticalEditText.text = with(
                    monitor.critical ?: ""
            ) { SpannableStringBuilder(this.toString()) }
            contentBinding.notificationIntervalEditText.text = with(
                    monitor.notificationInterval ?: ""
            ) { SpannableStringBuilder(this.toString()) }
        }
        contentBinding.scopesEditText.text = with(
                monitor.scopes.joinToString(", ")
        ) { SpannableStringBuilder(this) }
        contentBinding.excludeScopesEditText.text = with(
                monitor.excludeScopes.joinToString(", ")
        ) { SpannableStringBuilder(this) }
    }

    private fun checkValues(monitor: Monitor): Monitor {
        val duration = try {
            checkValue(monitor.duration, contentBinding.durationEditText.text.toString().toInt())
        } catch (_: NumberFormatException) {
            null
        }

        val warning = try {
            checkValue(monitor.warning, contentBinding.warningEditText.text.toString().toFloat())
        } catch (_: NumberFormatException) {
            null
        }

        val critical = try {
            checkValue(monitor.critical, contentBinding.criticalEditText.text.toString().toFloat())
        } catch (_: NumberFormatException) {
            null
        }

        val interval = try {
            checkValue(
                    monitor.notificationInterval,
                    contentBinding.notificationIntervalEditText.text.toString().toInt()
            )
        } catch (_: NumberFormatException) {
            null
        }

        return Monitor(
                monitor.id,
                monitor.type,
                checkValue(monitor.name, contentBinding.nameEditText.text.toString()),
                checkValue(monitor.service, contentBinding.serviceEditText.text.toString()),
                duration,
                checkValue(monitor.metric, contentBinding.metricEditText.text.toString()),
                checkValue(monitor.operator, contentBinding.operatorSpinner.selectedItem as String),
                warning,
                critical,
                interval,
                monitor.url,
                contentBinding.scopesEditText.text.toString()
                        .split(",").map(String::trim).toTypedArray(),
                contentBinding.excludeScopesEditText.text.toString()
                        .split(",").map(String::trim).toTypedArray()
        )
    }

    private fun <T> checkValue(v0: T, v1: T): T? {
        if (v0 != v1) {
            return v1
        }
        return null
    }

    companion object {

        private const val MONITOR_KEY = "MonitorKey"

        fun createIntent(context: Context, monitor: Monitor): Intent =
                Intent(context, MonitorEditActivity::class.java).apply {
                    putExtra(MONITOR_KEY, monitor)
                }
    }
}
