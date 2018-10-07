package jp.cordea.mackerelclient

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class ListItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val lp = view.layoutParams as RecyclerView.LayoutParams

        if (parent.getChildAdapterPosition(view) == 0) {
            lp.topMargin = context.resources.getDimension(R.dimen.card_margin).toInt()
        } else {
            lp.topMargin = 0
        }
    }
}
