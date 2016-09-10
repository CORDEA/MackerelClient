package jp.cordea.mackerelclient.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.bindView
import io.realm.Realm
import jp.cordea.mackerelclient.BuildConfig
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.LicenseActivity
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.model.UserMetric
import jp.cordea.mackerelclient.utils.StatusUtils
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Yoshihiro Tanaka on 16/01/14.
 */
class SettingFragment : android.support.v4.app.Fragment() {

    val hostCell: View by bindView(R.id.host_cell)
    val hostCellDetail: TextView by bindView(R.id.host_cell_detail)
    val initCell: View by bindView(R.id.init_cell)
    val licenseCell: View by bindView(R.id.license_cell)
    val contributorCell: View by bindView(R.id.contributor_cell)
    val version: TextView by bindView(R.id.version)

    var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_setting, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscription?.let {
            it.unsubscribe()
        }
        subscription = Observable
                .just(Unit)
                .subscribeOn(Schedulers.newThread())
                .map {
                    Realm.getDefaultInstance()
                }
                .map {
                    if (it.where(DisplayHostState::class.java).findAll().size == 0) {
                        it.executeTransaction {
                            for (key in resources.getStringArray(R.array.setting_host_cell_arr)) {
                                val item = it.createObject(DisplayHostState::class.java)
                                item.name = key
                                item.isDisplay = (key.equals("standby") || key.equals("working"))
                            }
                        }
                    }
                    it.close()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    updateStatus()
                }
                .subscribe({
                    addEvents()
                }, {})

        licenseCell.setOnClickListener {
            val intent = Intent(context, LicenseActivity::class.java)
            startActivity(intent)
        }

        contributorCell.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contributorUrl))
            startActivity(intent)
        }

        version.text = BuildConfig.VERSION_NAME
    }

    private fun addEvents() {
        var realm: Realm
        hostCell.setOnClickListener {
            realm = Realm.getDefaultInstance()
            val items = realm.copyFromRealm(realm.where(DisplayHostState::class.java).findAll())
            realm.close()
            var lastItem = 0
            AlertDialog.Builder(context)
                    .setMultiChoiceItems(items
                            .map { it.name!! }
                            .map { StatusUtils.requestNameToString(it) }
                            .toTypedArray(),
                            BooleanArray(items.size, {i -> items[i].isDisplay!!}),
                            { dialog, which, flag ->
                                val inRealm = Realm.getDefaultInstance()
                                val item = items[which]
                                item.isDisplay = flag
                                inRealm.executeTransaction {
                                    it.copyToRealmOrUpdate(item)
                                }

                                lastItem = which
                                updateStatus(inRealm)
                                inRealm.close()
                            })
                    .setOnDismissListener {
                        val inRealm = Realm.getDefaultInstance()
                        val all = inRealm.where(DisplayHostState::class.java).findAll()
                        if (all.filter { it.isDisplay!! }.size == 0) {
                            AlertDialog
                                    .Builder(context)
                                    .setMessage(R.string.setting_status_select_limit_dialog_message)
                                    .show()
                            val wk = all.filter { it.name.equals(items[lastItem].name) }.first()
                            inRealm.executeTransaction {
                                wk.isDisplay = true
                            }
                            updateStatus(inRealm)
                        }
                        inRealm.close()
                    }.show()
        }

        initCell.setOnClickListener {
            AlertDialog
                    .Builder(context)
                    .setMessage(R.string.setting_init_dialog_title)
                    .setPositiveButton(R.string.setting_init_dialog_positive_button, { dialogInterface, i ->
                        realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            it.delete(UserMetric::class.java)
                        }
                        realm.close()
                    })
                    .show()
        }
    }

    private fun updateStatus(r: Realm? = null) {
        var needClose = false
        val realm: Realm
        if (r == null) {
            realm = Realm.getDefaultInstance()
            needClose = true
        } else {
            realm = r
        }
        hostCellDetail.text = realm.where(DisplayHostState::class.java).findAll()
                .filter { it.isDisplay!! }
                .map { it.name }
                .map { StatusUtils.requestNameToString(it!!) }
                .joinToString(", ")
        if (needClose) {
            realm.close()
        }
    }

    override fun onDestroy() {
        subscription?.let {
            it.unsubscribe()
        }
        super.onDestroy()
    }

    companion object {
        private val contributorUrl = "https://github.com/CORDEA/MackerelClient/graphs/contributors"

        fun newInstance(): SettingFragment {
            val fragment = SettingFragment()
            return fragment
        }
    }
}