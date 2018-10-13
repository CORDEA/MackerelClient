package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ActivityMetricsEditBinding
import jp.cordea.mackerelclient.viewmodel.MetricsEditViewModel

class MetricsEditActivity : AppCompatActivity() {

    private val viewModel by lazy { MetricsEditViewModel() }

    private var id = -1
    private var type: MetricsType? = null

    private lateinit var binding: ActivityMetricsEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_metrics_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra(USER_METRIC_KEY, -1)
        type = MetricsType.valueOf(intent.getStringExtra(TYPE_KEY))
        if (id != -1) {
            val metric = viewModel.getMetric(id)
            binding.labelEditText.setText(metric.label)
            binding.metricFirstEditText.setText(metric.metric0)
            binding.metricSecondEditText.setText(metric.metric1)
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
        val metricFirst = binding.metricFirstEditText.text.toString()
        val metricSecond = binding.metricSecondEditText.text.toString()
        if (metricFirst.isBlank()) {
            val label = binding.labelEditText.text.toString()
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

        viewModel.storeMetric(
            id, intent.getStringExtra(ID_KEY), type!!.name,
            binding.labelEditText.text.toString(), metricFirst, metricSecond
        )
        return true
    }

    companion object {

        const val REQUEST_CODE = 0

        private const val ID_KEY = "IdKey"
        private const val USER_METRIC_KEY = "UserMetricKey"
        private const val TYPE_KEY = "TypeKey"

        fun createIntent(
            context: Context,
            type: MetricsType,
            id: String,
            metricId: Int? = null
        ): Intent {
            val intent = Intent(context, MetricsEditActivity::class.java)
            metricId?.let {
                intent.putExtra(USER_METRIC_KEY, metricId)
            }
            intent.putExtra(ID_KEY, id)
            intent.putExtra(TYPE_KEY, type.name)
            return intent
        }
    }
}
