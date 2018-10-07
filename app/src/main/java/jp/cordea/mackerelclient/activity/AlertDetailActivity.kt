package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.databinding.ActivityDetailCommonBinding
import jp.cordea.mackerelclient.fragment.AlertCloseDialogFragment
import jp.cordea.mackerelclient.utils.DateUtils

class AlertDetailActivity : AppCompatActivity() {

    private var alert: Alert? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityDetailCommonBinding>(this, R.layout.activity_detail_common)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val alert = intent.getSerializableExtra(ALERT_KEY) as Alert

        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = DetailCommonAdapter(this, insertInfo(alert))
            it.addItemDecoration(ListItemDecoration(this))
        }

        this.alert = alert
    }

    private fun insertInfo(alert: Alert): List<List<Pair<String, Int>>> {
        val list: MutableList<MutableList<Pair<String, Int>>> = arrayListOf()
        var inner: MutableList<Pair<String, Int>> = arrayListOf()
        inner.add(alert.status to R.string.alert_detail_status)
        inner.add(
            DateUtils.stringDateFromEpoch(alert.openedAt) to R.string.alert_detail_opened_at
        )
        alert.closedAt?.let {
            inner.add(DateUtils.stringDateFromEpoch(it) to R.string.alert_detail_closed_at)
        }
        list.add(inner)

        inner = arrayListOf()
        alert.reason?.let {
            inner.add(it to R.string.alert_detail_reason)
        }
        inner.add(alert.type to R.string.alert_detail_type)
        alert.value?.let {
            inner.add(it.toString() to R.string.alert_detail_value)
        }
        alert.message?.let {
            inner.add(it to R.string.alert_detail_message)
        }
        list.add(inner)
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.alert_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_close -> {
                AlertCloseDialogFragment
                    .newInstance(alert!!)
                    .apply {
                        onSuccess = {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                    .show(supportFragmentManager, "")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private const val ALERT_KEY = "AlertKey"

        fun createIntent(context: Context, alert: Alert): Intent =
            Intent(context, AlertDetailActivity::class.java).apply {
                putExtra(ALERT_KEY, alert)
            }
    }
}