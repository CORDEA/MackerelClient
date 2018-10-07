package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import io.reactivex.subjects.PublishSubject
import jp.cordea.mackerelclient.PicassoCircularTransform
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.User
import jp.cordea.mackerelclient.databinding.ListItemUserBinding
import jp.cordea.mackerelclient.utils.GravatarUtils
import jp.cordea.mackerelclient.viewmodel.UserListItemViewModel

class UserAdapter(
    context: Context
) : ArrayAdapter<User>(
    context,
    R.layout.list_item_user
) {
    val onUserDeleteSucceeded = PublishSubject.create<Boolean>()

    private var items: List<User> = emptyList()
    private var own: String? = null

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
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
                onUserDeleteSucceeded.onNext(true)
            }
            GravatarUtils.getGravatarImage(
                items[position].email,
                context.resources.getDimensionPixelSize(R.dimen.user_thumbnail_size_small)
            )?.let { url ->
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

    override fun getItem(position: Int): User? = items[position]

    override fun getCount(): Int = items.size

    fun update(users: List<User>, own: String?) {
        items = users
        this.own = own
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) {
        val binding: ListItemUserBinding = ListItemUserBinding.bind(view)
    }
}
