package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ActivityMetricsEditBinding
import jp.cordea.mackerelclient.databinding.ContentMetricsEditBinding
import jp.cordea.mackerelclient.viewmodel.MetricsEditViewModel

class MetricsEditActivity : AppCompatActivity() {

    private val viewModel: MetricsEditViewModel by lazy {
        MetricsEditViewModel()
    }

    private var id: Int = -1

    private var type: MetricsType? = null

    private lateinit var contentBinding: ContentMetricsEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
                .setContentView<ActivityMetricsEditBinding>(this, R.layout.activity_metrics_edit)
        contentBinding = binding.content ?: return
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra(UserMetricKey, -1)
        type = MetricsType.valueOf(intent.getStringExtra(TypeKey))
        if (id != -1) {
            val metric = viewModel.getMetric(id)
            contentBinding.labelEditText.setText(metric.label)
            contentBinding.metricFirstEditText.setText(metric.metric0)
            contentBinding.metricSecondEditText.setText(metric.metric1)
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
        val metricFirst = contentBinding.metricFirstEditText.text.toString()
        val metricSecond = contentBinding.metricSecondEditText.text.toString()
        if (metricFirst.isBlank()) {
            val label = contentBinding.labelEditText.text.toString()
            return if (label.isBlank() && metricSecond.isBlank()) {
                false
            } else {
                AlertDialog
                        .Builder(this)
                        .setMessage(R.string.metrics_edit_dialog_title)
                        .show()
                null
            }
        }

        viewModel.storeMetric(id, intent.getStringExtra(IdKey), type!!.name,
                contentBinding.labelEditText.text.toString(), metricFirst, metricSecond)
        return true
    }

    companion object {

        const val RequestCode = 0

        private const val IdKey = "IdKey"

        private const val UserMetricKey = "UserMetricKey"

        private const val TypeKey = "TypeKey"

        fun createIntent(
                context: Context,
                type: MetricsType,
                id: String,
                metricId: Int? = null
        ): Intent {
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
