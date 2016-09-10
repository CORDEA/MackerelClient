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
import io.realm.Realm
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.model.UserMetric

class MetricsEditActivity : AppCompatActivity() {
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

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val label: TextView by bindView(R.id.label)
    val metric0: TextView by bindView(R.id.metric_0)
    val metric1: TextView by bindView(R.id.metric_1)

    var id: Int = -1
    var type: MetricsType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metrics_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra(UserMetricKey, -1)
        type = MetricsType.valueOf(intent.getStringExtra(TypeKey))
        if (id != -1) {
            val realm = Realm.getDefaultInstance()
            val metric = realm.copyFromRealm(realm.where(UserMetric::class.java).equalTo("id", id).findFirst())
            realm.close()

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

        val realm = Realm.getDefaultInstance()
        val maxId = realm.where(UserMetric::class.java).max("id")
        val item = UserMetric()
        item.id = if (id != -1) id else if (maxId == null) 0 else (maxId.toInt() + 1)
        item.parentId = intent.getStringExtra(IdKey)
        item.type = type!!.name
        item.label = label.text.toString()
        item.metric0 = metric0.text.toString()
        if (!m1.isNullOrBlank()) {
            item.metric1 = metric1.text.toString()
        }

        realm.executeTransaction {
            realm.copyToRealmOrUpdate(item)
        }
        realm.close()
        return true
    }


}
