package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ogaclejapan.rx.binding.RxEvent
import com.pawegio.kandroid.alert
import com.pawegio.kandroid.find
import com.pawegio.kandroid.inflateLayout
import com.squareup.picasso.Picasso
import jp.cordea.mackerelclient.PicassoCircularTransform
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.User
import jp.cordea.mackerelclient.utils.DialogUtils
import jp.cordea.mackerelclient.utils.GravatarUtils
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Yoshihiro Tanaka on 16/01/14.
 */
class UserAdapter(context: Context, val items: List<User>, val own: String?) : ArrayAdapter<User>(context, R.layout.list_item_user) {

    public val onUserDeleteSucceed = RxEvent.create<Boolean>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = convertView ?: context.inflateLayout(R.layout.list_item_user, parent)
        val name: TextView = view.find(R.id.name)
        val email: TextView = view.find(R.id.email)
        val delete: ImageView = view.find(R.id.delete_button)
        val thumbnail: ImageView = view.find(R.id.user_thumbnail)
        GravatarUtils.getGravatarImage(items[position].email,
                context.resources.getDimensionPixelSize(R.dimen.user_thumbnail_size_small))?.let { url ->
            Picasso.with(context)
                    .load(url)
                    .transform(PicassoCircularTransform())
                    .into(thumbnail)
        }
        name.text = items[position].screenName
        email.text = items[position].email
        var isOwn = false
        own?.let {
            if (it.equals(items[position].email)) {
                isOwn = true
                delete.visibility = View.GONE
            }
        }
        if (!isOwn) {
            delete.setOnClickListener {
                context.alert {
                    message(R.string.user_delete_dialog_title)
                    positiveButton(R.string.delete_positive_button, {
                        val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                        dialog.show()
                        MackerelApiClient
                                .deleteUser(context, items[position].id)
                                .enqueue(object : Callback<User> {
                                    override fun onResponse(response: Response<User>?) {
                                        dialog.dismiss()
                                        response?.let {
                                            val success = DialogUtils.switchDialog(context, it,
                                                    R.string.user_delete_error_dialog_title,
                                                    R.string.error_403_dialog_message)
                                            if (success) {
                                                onUserDeleteSucceed.post(true)
                                            }
                                            return
                                        }
                                        DialogUtils.showDialog(context,
                                                R.string.user_delete_error_dialog_title)
                                    }

                                    override fun onFailure(t: Throwable?) {
                                        dialog.dismiss()
                                        DialogUtils.showDialog(context,
                                                R.string.user_delete_error_dialog_title)
                                    }
                                })
                    })
                }.show()
            }
        }

        return view
    }

    override fun getItem(position: Int): User? {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }
}