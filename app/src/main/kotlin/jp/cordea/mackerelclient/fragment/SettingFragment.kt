package jp.cordea.mackerelclient.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import jp.cordea.mackerelclient.BuildConfig
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.LicenseActivity
import jp.cordea.mackerelclient.databinding.FragmentSettingBinding
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.model.UserMetric
import jp.cordea.mackerelclient.utils.StatusUtils
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SettingFragment : Fragment() {

    private var subscription: Subscription? = null

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View =
            FragmentSettingBinding.inflate(inflater, container, false).also {
                binding = it
            }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscription?.let(Subscription::unsubscribe)
        subscription = Observable
                .just(Unit)
                .subscribeOn(Schedulers.newThread())
                .map { Realm.getDefaultInstance() }
                .map { realm ->
                    if (realm.where(DisplayHostState::class.java).findAll().size == 0) {
                        realm.executeTransaction {
                            for (key in resources.getStringArray(R.array.setting_host_cell_arr)) {
                                val item = it.createObject(DisplayHostState::class.java, key)
                                item.isDisplay = (key == "standby" || key == "working")
                            }
                        }
                    }
                    realm.close()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map { updateStatus() }
                .subscribe({ addEvents() }, {})

        binding.licenseLayout.setOnClickListener {
            val intent = Intent(context, LicenseActivity::class.java)
            startActivity(intent)
        }

        binding.contributorLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CONTRIBUTORS_URL))
            startActivity(intent)
        }

        binding.versionTextView.text = BuildConfig.VERSION_NAME
    }

    private fun addEvents() {
        var realm: Realm
        binding.hostLayout.setOnClickListener { _ ->
            realm = Realm.getDefaultInstance()
            val items = realm.copyFromRealm(realm.where(DisplayHostState::class.java).findAll())
            realm.close()
            var lastItem = 0
            AlertDialog.Builder(context)
                    .setMultiChoiceItems(
                            items.map { StatusUtils.requestNameToString(it.name) }.toTypedArray(),
                            BooleanArray(items.size) { i -> items[i].isDisplay!! }
                    ) { _, which, flag ->
                        val inRealm = Realm.getDefaultInstance()
                        val item = items[which]
                        item.isDisplay = flag
                        inRealm.executeTransaction {
                            it.copyToRealmOrUpdate(item)
                        }

                        lastItem = which
                        updateStatus(inRealm)
                        inRealm.close()
                    }
                    .setOnDismissListener { _ ->
                        val inRealm = Realm.getDefaultInstance()
                        val all = inRealm.where(DisplayHostState::class.java).findAll()
                        if (all.none { it.isDisplay!! }) {
                            AlertDialog
                                    .Builder(context)
                                    .setMessage(R.string.setting_status_select_limit_dialog_message)
                                    .show()
                            val wk = all.first { it.name == items[lastItem].name }
                            inRealm.executeTransaction {
                                wk.isDisplay = true
                            }
                            updateStatus(inRealm)
                        }
                        inRealm.close()
                    }.show()
        }

        binding.initLayout.setOnClickListener { _ ->
            AlertDialog
                    .Builder(context)
                    .setMessage(R.string.setting_init_dialog_title)
                    .setPositiveButton(R.string.setting_init_dialog_positive_button) { _, _ ->
                        realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            it.delete(UserMetric::class.java)
                        }
                        realm.close()
                    }
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
        binding.hostTextView.text = realm.where(DisplayHostState::class.java).findAll()
                .filter { it.isDisplay!! }
                .map { it.name }
                .joinToString(", ") { StatusUtils.requestNameToString(it) }
        if (needClose) {
            realm.close()
        }
    }

    override fun onDestroy() {
        subscription?.let(Subscription::unsubscribe)
        super.onDestroy()
    }

    companion object {

        private const val CONTRIBUTORS_URL =
                "https://github.com/CORDEA/MackerelClient/graphs/contributors"

        fun newInstance(): SettingFragment =
                SettingFragment()
    }
}
