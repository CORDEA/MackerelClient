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
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.DetailCommonAdapter
import jp.cordea.mackerelclient.databinding.ActivityDetailCommonBinding
import jp.cordea.mackerelclient.fragment.AlertCloseDialogLoader
import jp.cordea.mackerelclient.fragment.AlertCloseResult
import jp.cordea.mackerelclient.model.DisplayableAlert
import jp.cordea.mackerelclient.utils.DateUtils
import retrofit2.HttpException
import javax.inject.Inject

class AlertDetailActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var dialogLoader: AlertCloseDialogLoader

    private lateinit var binding: ActivityDetailCommonBinding

    private var disposable: Disposable? = null

    private val alert by lazy { intent.getParcelableExtra<DisplayableAlert>(ALERT_KEY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_common)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = DetailCommonAdapter(this, insertInfo(alert))
            it.addItemDecoration(ListItemDecoration(this))
        }

        disposable = dialogLoader.closeAlertCloseDialog
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                when (it) {
                    AlertCloseResult.Success -> {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    is AlertCloseResult.Error -> {
                        val throwable = it.throwable
                        var message = R.string.alert_detail_error_message
                        if (throwable is HttpException) {
                            if (throwable.code() == 403) {
                                message = R.string.alert_detail_403_error_message
                            }
                        }
                        Snackbar.make(
                            binding.root,
                            message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    private fun insertInfo(alert: DisplayableAlert): List<List<Pair<String, Int>>> {
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
            R.id.action_close -> dialogLoader.show(alert)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    companion object {
        private const val ALERT_KEY = "AlertKey"

        fun createIntent(context: Context, alert: DisplayableAlert): Intent =
            Intent(context, AlertDetailActivity::class.java).apply {
                putExtra(ALERT_KEY, alert)
            }
    }
}
