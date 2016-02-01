package jp.cordea.mackerelclient.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import jp.cordea.mackerelclient.R

/**
 * Created by Yoshihiro Tanaka on 16/01/19.
 */
class CharCircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val colorSet: Map<Char, Int> = mapOf(
            Pair('C', R.color.statusCritical),
            Pair('O', R.color.statusOk),
            Pair('W', R.color.statusWarning),
            Pair('U', R.color.statusUnknown)
    )
    private var char: Char = 'U'

    public fun setChar(char: Char) {
        this.char = char
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paint = Paint()
        paint.color = ContextCompat.getColor(context, colorSet[char] ?: R.color.statusUnknown)
        paint.isAntiAlias = true

        val size = width / 2.0f
        canvas.drawCircle(size, size, size, paint)

        paint.textSize = resources.getDimension(R.dimen.char_circle_font_size)
        paint.textAlign = Paint.Align.CENTER
        val type = Typeface.create("sans-serif-light", Typeface.NORMAL)
        paint.setTypeface(type)
        paint.color = ContextCompat.getColor(context, android.R.color.white)
        canvas.drawText(char.toString(), size, size - ((paint.ascent() + paint.descent()) / 2.0f), paint)
    }
}