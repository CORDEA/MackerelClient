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

class SettingFragment : Fragment(), SettingStatusSelectionDialogFragment.OnUpdateStatusListener {

    private val realm = Realm.getDefaultInstance()

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
        subscription?.unsubscribe()
        subscription = Observable
            .just(Unit)
            .subscribeOn(Schedulers.io())
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
            .map { onUpdateStatus() }
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

    override fun onDestroy() {
        super.onDestroy()
        subscription?.unsubscribe()
        realm.close()
    }

    override fun onUpdateStatus() {
        binding.hostTextView.text = realm.where(DisplayHostState::class.java).findAll()
            .filter { it.isDisplay!! }
            .map { it.name }
            .joinToString(", ") { StatusUtils.requestNameToString(it) }
    }

    private fun addEvents() {
        binding.hostLayout.setOnClickListener { _ ->
            SettingStatusSelectionDialogFragment().show(
                childFragmentManager,
                SettingStatusSelectionDialogFragment.TAG
            )
        }

        binding.initLayout.setOnClickListener { _ ->
            AlertDialog
                .Builder(context)
                .setMessage(R.string.setting_init_dialog_title)
                .setPositiveButton(R.string.setting_init_dialog_positive_button) { _, _ ->
                    realm.executeTransaction {
                        it.delete(UserMetric::class.java)
                    }
                }
                .show()
        }
    }

    companion object {

        private const val CONTRIBUTORS_URL =
            "https://github.com/CORDEA/MackerelClient/graphs/contributors"

        fun newInstance(): SettingFragment = SettingFragment()
    }
}