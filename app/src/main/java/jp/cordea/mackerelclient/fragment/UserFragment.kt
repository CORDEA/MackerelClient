package jp.cordea.mackerelclient.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.realm.Realm
import jp.cordea.mackerelclient.adapter.UserAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.databinding.FragmentUserBinding
import jp.cordea.mackerelclient.model.Preferences
import jp.cordea.mackerelclient.model.UserKey
import javax.inject.Inject

class UserFragment : Fragment() {

    @Inject
    lateinit var apiClient: MackerelApiClient

    private lateinit var binding: FragmentUserBinding

    private val adapter by lazy { UserAdapter(context!!) }

    private var refreshDisposable = SerialDisposable()
    private var disposable: Disposable? = null

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

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

        disposable = adapter.onUserDeleteSucceeded
            .filter { it }
            .subscribe({ refresh() }, { })

        refresh()
        binding.swipeRefresh.setOnRefreshListener {
            refresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        refreshDisposable.dispose()
    }

    private fun refresh() {
        apiClient
            .getUsers()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ users ->
                binding.swipeRefresh.isRefreshing = false
                val userId = Preferences(context!!).userId
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
            .run(refreshDisposable::set)
    }

    companion object {
        fun newInstance(): UserFragment = UserFragment()
    }
}
