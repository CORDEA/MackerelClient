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
import butterknife.bindView
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.api.response.Host
import jp.cordea.mackerelclient.fragment.HostRetireDialogFragment
import jp.cordea.mackerelclient.utils.DateUtils
import jp.cordea.mackerelclient.utils.StatusUtils
import rx.Subscription

class HostDetailActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    var host: Host? = null

    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_common)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val host = intent.getParcelableExtra<Host>(HostKey)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DetailCommonAdapter(this, createData(host))
        recyclerView.addItemDecoration(ListItemDecoration(this))

        this.host = host
    }

    private fun createData(host: Host): List<List<Pair<String, Int>>> {
        val list: MutableList<MutableList<Pair<String, Int>>> = arrayListOf()
        var inner: MutableList<Pair<String, Int>> = arrayListOf()

        inner.add(Pair(StatusUtils.requestNameToString(host.status!!), R.string.host_detail_status))
        inner.add(Pair(host.memo!!, R.string.host_detail_memo))
        list.add(inner)

        inner = arrayListOf()
        inner.add(Pair(
                host.roles.size.let {
                    if (it <= 1) resources.getString(R.string.format_role).format(it)
                    else
                        if (it > 99) resources.getString(R.string.format_roles_ex)
                        else resources.getString(R.string.format_roles).format(it)
                },
                R.string.host_detail_roles
        ))
        inner.add(Pair(DateUtils.stringDateFromEpoch(host.createdAt!!), R.string.host_detail_created_at))
        list.add(inner)
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.host_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPause() {
        super.onPause()
        subscription?.let(Subscription::unsubscribe)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_retire -> {
                HostRetireDialogFragment
                        .newInstance(host!!)
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

        public val RequestCode = 0

        private val HostKey = "HostKey"

        fun createIntent(context: Context, host: Host): Intent {
            return Intent(context, HostDetailActivity::class.java).apply {
                putExtra(HostKey, host)
            }
        }
    }
}
