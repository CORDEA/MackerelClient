package jp.cordea.mackerelclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import io.realm.Realm
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.UserAdapter
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.model.Preferences
import jp.cordea.mackerelclient.model.UserKey
import kotterknife.bindView
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by Yoshihiro Tanaka on 16/01/14.
 */
class UserFragment : Fragment() {

    val errView: View by bindView(R.id.error)

    val progress: View by bindView(R.id.progress)

    val listView : ListView by bindView(R.id.list_view)

    val swipeRefresh: SwipeRefreshLayout by bindView(R.id.swipe_refresh)

    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_user, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscription?.let(Subscription::unsubscribe)
        subscription = refresh()
        swipeRefresh.setOnRefreshListener {
            subscription?.let(Subscription::unsubscribe)
            subscription = refresh()
        }
    }

    private fun refresh(): Subscription {
        return MackerelApiClient
                .getUsers(context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    swipeRefresh.isRefreshing = false
                    val userId = Preferences(context).userId
                    val realm = Realm.getDefaultInstance()
                    val user = realm.copyFromRealm(realm.where(UserKey::class.java).equalTo("id", userId).findFirst())
                    realm.close()

                    val adapter = UserAdapter(context, it.users, user.email)
                    listView.adapter = adapter
                    adapter.onUserDeleteSucceeded
                            .asObservable()
                            .filter { it }
                            .doOnNext {
                                refresh()
                            }
                            .subscribe({},{})
                    progress.visibility = View.GONE
                    swipeRefresh.visibility = View.VISIBLE
                }, {
                    swipeRefresh.isRefreshing = false
                    errView.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                    swipeRefresh.visibility = View.GONE
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.let(Subscription::unsubscribe)
    }

    companion object {
        fun newInstance(): UserFragment {
            val fragment = UserFragment()
            return fragment
        }
    }
}