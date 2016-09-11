package jp.cordea.mackerelclient

import jp.cordea.mackerelclient.R
import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by CORDEA on 2016/09/10.
 */
class ListItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        view?.let {
            parent?.let { parent ->
                val lp = it.layoutParams as RecyclerView.LayoutParams

                if (parent.getChildAdapterPosition(it) == 0) {
                    lp.topMargin = context.resources.getDimension(R.dimen.card_margin).toInt()
                } else {
                    lp.topMargin = 0
                }
            }

        }
    }

}