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
import jp.cordea.mackerelclient.api.response.Host
import jp.cordea.mackerelclient.databinding.ActivityDetailCommonBinding
import jp.cordea.mackerelclient.fragment.HostRetireDialogFragment
import jp.cordea.mackerelclient.utils.DateUtils
import jp.cordea.mackerelclient.utils.StatusUtils
import rx.Subscription

class HostDetailActivity : AppCompatActivity() {

    var host: Host? = null

    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityDetailCommonBinding>(this, R.layout.activity_detail_common)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val host = intent.getSerializableExtra(HOST_KEY) as Host

        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = DetailCommonAdapter(this, createData(host))
            it.addItemDecoration(ListItemDecoration(this))
        }

        this.host = host
    }

    private fun createData(host: Host): List<List<Pair<String, Int>>> {
        val list: MutableList<MutableList<Pair<String, Int>>> = arrayListOf()
        var inner: MutableList<Pair<String, Int>> = arrayListOf()

        inner.add(StatusUtils.requestNameToString(host.status) to R.string.host_detail_status)
        inner.add(host.memo to R.string.host_detail_memo)
        list.add(inner)

        inner = arrayListOf()
        inner.add(
            host.roles.size.let {
                if (it <= 1) resources.getString(R.string.format_role).format(it)
                else if (it > 99) resources.getString(R.string.format_roles_ex)
                else resources.getString(R.string.format_roles).format(it)
            } to R.string.host_detail_roles
        )
        inner.add(
            DateUtils.stringDateFromEpoch(host.createdAt) to R.string.host_detail_created_at
        )
        list.add(inner)
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.host_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPause() {
        super.onPause()
        subscription?.unsubscribe()
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

        const val REQUEST_CODE = 0

        private const val HOST_KEY = "HostKey"

        fun createIntent(context: Context, host: Host): Intent =
            Intent(context, HostDetailActivity::class.java).apply {
                putExtra(HOST_KEY, host)
            }
    }
}
