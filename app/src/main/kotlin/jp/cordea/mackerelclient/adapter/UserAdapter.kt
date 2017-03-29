package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ogaclejapan.rx.binding.RxEvent
import com.squareup.picasso.Picasso
import jp.cordea.mackerelclient.PicassoCircularTransform
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.User
import jp.cordea.mackerelclient.utils.GravatarUtils
import jp.cordea.mackerelclient.viewmodel.UserListItemViewModel

/**
 * Created by Yoshihiro Tanaka on 16/01/14.
 */
class UserAdapter(context: Context, val items: List<User>, val own: String?) : ArrayAdapter<User>(context, R.layout.list_item_user) {

    val onUserDeleteSucceeded: RxEvent<Boolean> = RxEvent.create<Boolean>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val viewModel = UserListItemViewModel(context, items[position])
        viewModel.onUserDeleteSucceeded = {
            onUserDeleteSucceeded.post(true)
        }
        GravatarUtils.getGravatarImage(items[position].email,
                context.resources.getDimensionPixelSize(R.dimen.user_thumbnail_size_small))?.let { url ->
            Picasso.with(context)
                    .load(url)
                    .transform(PicassoCircularTransform())
                    .into(viewHolder.thumbnailImageView)
        }
        viewHolder.nameTextView.text = items[position].screenName
        viewHolder.emailTextView.text = items[position].email

        var isOwn = false
        own?.let {
            if (it == items[position].email) {
                isOwn = true
                viewHolder.deleteImageView.visibility = View.GONE
            }
        }
        if (!isOwn) {
            viewHolder.deleteImageView.setOnClickListener(viewModel.deleteButtonOnClick)
        }
        return view
    }

    override fun getItem(position: Int): User? {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) {

        val nameTextView = view.findViewById(R.id.name) as TextView

        val emailTextView = view.findViewById(R.id.email) as TextView

        val deleteImageView = view.findViewById(R.id.delete_button) as ImageView

        val thumbnailImageView = view.findViewById(R.id.user_thumbnail) as ImageView

    }
}