package jp.cordea.mackerelclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import jp.cordea.mackerelclient.PicassoCircularTransform
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.User
import jp.cordea.mackerelclient.databinding.ListItemUserBinding
import jp.cordea.mackerelclient.di.FragmentScope
import jp.cordea.mackerelclient.fragment.UserDeleteConfirmDialogFragment
import jp.cordea.mackerelclient.utils.GravatarUtils
import javax.inject.Inject

@FragmentScope
class UserAdapter @Inject constructor(
    private val fragment: Fragment
) : ArrayAdapter<User>(
    fragment.context!!,
    R.layout.list_item_user
) {
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

        val item = items[position]
        viewHolder.binding.run {
            GravatarUtils.getGravatarImage(
                item.email,
                context.resources.getDimensionPixelSize(R.dimen.user_thumbnail_size_small)
            )?.let { url ->
                Picasso.with(context)
                    .load(url)
                    .transform(PicassoCircularTransform())
                    .into(userThumbnailImageView)
            }
            nameTextView.text = item.screenName
            emailTextView.text = item.email

            var isOwn = false
            own?.let {
                if (it == item.email) {
                    isOwn = true
                    deleteImageView.visibility = View.GONE
                }
            }
            if (!isOwn) {
                deleteImageView.setOnClickListener {
                    UserDeleteConfirmDialogFragment.newInstance(item.id)
                        .show(fragment.childFragmentManager, UserDeleteConfirmDialogFragment.TAG)
                }
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
