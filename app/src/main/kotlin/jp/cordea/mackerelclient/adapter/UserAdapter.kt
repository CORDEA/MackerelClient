package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ogaclejapan.rx.binding.RxEvent
import com.squareup.picasso.Picasso
import jp.cordea.mackerelclient.PicassoCircularTransform
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.User
import jp.cordea.mackerelclient.databinding.ListItemUserBinding
import jp.cordea.mackerelclient.utils.GravatarUtils
import jp.cordea.mackerelclient.viewmodel.UserListItemViewModel

class UserAdapter(
        context: Context,
        val items: List<User>,
        private val own: String?
) : ArrayAdapter<User>(
        context,
        R.layout.list_item_user
) {

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

        viewHolder.binding.run {
            val viewModel = UserListItemViewModel(context, items[position])
            viewModel.onUserDeleteSucceeded = {
                onUserDeleteSucceeded.post(true)
            }
            GravatarUtils.getGravatarImage(items[position].email,
                    context.resources.getDimensionPixelSize(R.dimen.user_thumbnail_size_small))?.let { url ->
                Picasso.with(context)
                        .load(url)
                        .transform(PicassoCircularTransform())
                        .into(userThumbnailImageView)
            }
            nameTextView.text = items[position].screenName
            emailTextView.text = items[position].email

            var isOwn = false
            own?.let {
                if (it == items[position].email) {
                    isOwn = true
                    deleteImageView.visibility = View.GONE
                }
            }
            if (!isOwn) {
                deleteImageView.setOnClickListener(viewModel.deleteButtonOnClick)
            }
        }
        return view
    }

    override fun getItem(position: Int): User? =
            items[position]

    override fun getCount(): Int =
            items.size

    class ViewHolder(view: View) {

        val binding: ListItemUserBinding = ListItemUserBinding.bind(view)
    }
}
