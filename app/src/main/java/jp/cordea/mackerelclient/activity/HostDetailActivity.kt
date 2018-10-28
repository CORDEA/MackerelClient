package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.databinding.ActivityDetailCommonBinding
import jp.cordea.mackerelclient.fragment.HostRetireDialogFragment
import jp.cordea.mackerelclient.model.DisplayableHost
import jp.cordea.mackerelclient.utils.DateUtils
import jp.cordea.mackerelclient.utils.StatusUtils
import javax.inject.Inject

class HostDetailActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var host: DisplayableHost

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityDetailCommonBinding>(this, R.layout.activity_detail_common)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        host = intent.getParcelableExtra(HOST_KEY)
        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = DetailCommonAdapter(this, createData(host))
            it.addItemDecoration(ListItemDecoration(this))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.host_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_retire -> {
                HostRetireDialogFragment
                    .newInstance(host)
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

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    private fun createData(host: DisplayableHost): List<List<Pair<String, Int>>> {
        val list: MutableList<MutableList<Pair<String, Int>>> = arrayListOf()
        var inner: MutableList<Pair<String, Int>> = arrayListOf()

        inner.add(StatusUtils.requestNameToString(host.status) to R.string.host_detail_status)
        inner.add(host.memo to R.string.host_detail_memo)
        list.add(inner)

        inner = arrayListOf()
        inner.add(
            host.numberOfRoles.let {
                when {
                    it <= 1 -> resources.getString(R.string.format_role).format(it)
                    it > 99 -> resources.getString(R.string.format_roles_ex)
                    else -> resources.getString(R.string.format_roles).format(it)
                }
            } to R.string.host_detail_roles
        )
        inner.add(
            DateUtils.stringDateFromEpoch(host.createdAt) to R.string.host_detail_created_at
        )
        list.add(inner)
        return list
    }

    companion object {
        const val REQUEST_CODE = 0

        private const val HOST_KEY = "HostKey"

        fun createIntent(context: Context, host: DisplayableHost): Intent =
            Intent(context, HostDetailActivity::class.java).apply {
                putExtra(HOST_KEY, host)
            }
    }
}
