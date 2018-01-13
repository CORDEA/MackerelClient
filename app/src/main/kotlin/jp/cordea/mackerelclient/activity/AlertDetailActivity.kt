package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.fragment.AlertCloseDialogFragment
import jp.cordea.mackerelclient.utils.DateUtils
import kotterknife.bindView

class AlertDetailActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    private var alert: Alert? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_common)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val alert = intent.getSerializableExtra(AlertKey) as Alert

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DetailCommonAdapter(this, insertInfo(alert))
        recyclerView.addItemDecoration(ListItemDecoration(this))

        this.alert = alert
    }

    private fun insertInfo(alert: Alert): List<List<Pair<String, Int>>> {
        val list: MutableList<MutableList<Pair<String, Int>>> = arrayListOf()
        var inner: MutableList<Pair<String, Int>> = arrayListOf()
        inner.add(Pair(alert.status, R.string.alert_detail_status))
        inner.add(Pair(DateUtils.stringDateFromEpoch(alert.openedAt), R.string.alert_detail_opened_at))
        alert.closedAt?.let {
            inner.add(Pair(DateUtils.stringDateFromEpoch(it), R.string.alert_detail_closed_at))
        }
        list.add(inner)

        inner = arrayListOf()
        alert.reason?.let {
            inner.add(Pair(it, R.string.alert_detail_reason))
        }
        inner.add(Pair(alert.type, R.string.alert_detail_type))
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

        private val AlertKey = "AlertKey"

        fun createIntent(context: Context, alert: Alert): Intent {
            return Intent(context, AlertDetailActivity::class.java).apply {
                putExtra(AlertKey, alert)
            }
        }
    }
}
