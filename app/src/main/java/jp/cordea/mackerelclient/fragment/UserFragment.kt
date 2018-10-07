package jp.cordea.mackerelclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import jp.cordea.mackerelclient.adapter.UserAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.databinding.FragmentUserBinding
import jp.cordea.mackerelclient.model.Preferences
import jp.cordea.mackerelclient.model.UserKey
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding

    private val adapter by lazy { UserAdapter(context!!) }

    private var refreshSubscription: Subscription? = null
    private var subscription: Subscription? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentUserBinding.inflate(inflater, container, false).also {
            setHasOptionsMenu(true)
            binding = it
        }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.listView.adapter = adapter

        subscription = adapter.onUserDeleteSucceeded
            .asObservable()
            .filter { it }
            .subscribe({ refresh() }, { })

        refreshSubscription = refresh()
        binding.swipeRefresh.setOnRefreshListener {
            refreshSubscription?.unsubscribe()
            refreshSubscription = refresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.unsubscribe()
        refreshSubscription?.unsubscribe()
    }

    private fun refresh(): Subscription {
        val context = context!!
        return MackerelApiClient
            .getUsers(context)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ users ->
                binding.swipeRefresh.isRefreshing = false
                val userId = Preferences(context).userId
                val realm = Realm.getDefaultInstance()
                val user = realm.copyFromRealm(
                    realm.where(UserKey::class.java).equalTo("id", userId).findFirst()!!
                )
                realm.close()

                adapter.update(users.users, user.email)
                binding.progressLayout.visibility = View.GONE
                binding.swipeRefresh.visibility = View.VISIBLE
            }, {
                binding.swipeRefresh.isRefreshing = false
                binding.error.root.visibility = View.VISIBLE
                binding.progressLayout.visibility = View.GONE
                binding.swipeRefresh.visibility = View.GONE
            })
    }

    companion object {
        fun newInstance(): UserFragment = UserFragment()
    }
}
