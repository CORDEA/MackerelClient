package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import butterknife.bindView
import jp.cordea.mackerelclient.viewmodel.MetricsEditViewModel
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R

class MetricsEditActivity : AppCompatActivity() {

    private val viewModel: MetricsEditViewModel by lazy {
        MetricsEditViewModel(this)
    }

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val label: TextView by bindView(R.id.label)

    val metric0: TextView by bindView(R.id.metric_0)

    val metric1: TextView by bindView(R.id.metric_1)

    private var id: Int = -1

    private var type: MetricsType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metrics_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra(UserMetricKey, -1)
        type = MetricsType.valueOf(intent.getStringExtra(TypeKey))
        if (id != -1) {
            val metric = viewModel.getMetric(id)
            label.text = metric.label
            metric0.text = metric.metric0
            metric1.text = metric.metric1
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                saveMetric()?.let {
                    if (it) {
                        setResult(Activity.RESULT_OK)
                    } else {
                        setResult(Activity.RESULT_CANCELED)
                    }
                    finish()
                }
            }
            R.id.action_discard -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        saveMetric()?.let {
            if (it) {
                setResult(Activity.RESULT_OK)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.metric_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun saveMetric(): Boolean? {
        val m0 = metric0.text.toString()
        val m1 = metric1.text.toString()
        if (m0.isNullOrBlank()) {
            if (label.text.toString().isNullOrBlank() && m1.isNullOrBlank()) {
                return false
            } else {
                AlertDialog
                        .Builder(this)
                        .setMessage(R.string.metrics_edit_dialog_title)
                        .show()
                return null
            }
        }

        viewModel.storeMetric(id, intent.getStringExtra(IdKey), type!!.name,
                label.text.toString(), m0, m1)
        return true
    }

    companion object {

        public val RequestCode = 0

        private val IdKey = "IdKey"

        private val UserMetricKey = "UserMetricKey"

        private val TypeKey = "TypeKey"

        fun newInstance(context: Context, type: MetricsType, id: String, metricId: Int? = null): Intent {
            val intent = Intent(context, MetricsEditActivity::class.java)
            metricId?.let {
                intent.putExtra(UserMetricKey, metricId)
            }
            intent.putExtra(IdKey, id)
            intent.putExtra(TypeKey, type.name)
            return intent
        }
    }
}
