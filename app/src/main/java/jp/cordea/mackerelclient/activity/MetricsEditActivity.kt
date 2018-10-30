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
import dagger.Lazy
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ActivityMetricsEditBinding
import jp.cordea.mackerelclient.viewmodel.MetricsEditViewModel
import javax.inject.Inject

class MetricsEditActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelProvider: Lazy<MetricsEditViewModel>

    private val metricId by lazy { intent.getIntExtra(USER_METRIC_KEY, -1) }
    private val id by lazy { intent.getStringExtra(ID_KEY) }

    private val viewModel get() = viewModelProvider.get()
    private val compositeDisposable = CompositeDisposable()

    private var type: MetricsType? = null

    private lateinit var binding: ActivityMetricsEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_metrics_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.labelText
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { binding.labelEditText.setText(it) }
            .addTo(compositeDisposable)

        viewModel.metricFirstText
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { binding.metricFirstEditText.setText(it) }
            .addTo(compositeDisposable)

        viewModel.metricSecondText
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { binding.metricSecondEditText.setText(it) }
            .addTo(compositeDisposable)

        type = MetricsType.valueOf(intent.getStringExtra(TYPE_KEY))
        if (metricId != -1) {
            viewModel.start(metricId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
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
            metricId,
            id,
            type!!.name,
            binding.labelEditText.text.toString(),
            metricFirst,
            metricSecond
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
            metricId: Int = -1
        ): Intent =
            Intent(context, MetricsEditActivity::class.java).apply {
                putExtra(USER_METRIC_KEY, metricId)
                putExtra(ID_KEY, id)
                putExtra(TYPE_KEY, type.name)
            }
    }
}
