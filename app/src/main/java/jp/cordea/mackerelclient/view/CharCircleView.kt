package jp.cordea.mackerelclient.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import jp.cordea.mackerelclient.R

class CharCircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    var char: Char = 'U'
        set(value) {
            field = value
            invalidate()
        }

    private val colorSet: Map<Char, Int> = mapOf(
        'C' to R.color.statusCritical,
        'O' to R.color.statusOk,
        'W' to R.color.statusWarning,
        'U' to R.color.statusUnknown
    )

    private val paint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.reset()
        paint.color = ContextCompat.getColor(context, colorSet[char] ?: R.color.statusUnknown)
        paint.isAntiAlias = true

        val size = width / 2.0f
        canvas.drawCircle(size, size, size, paint)

        paint.textSize = resources.getDimension(R.dimen.char_circle_font_size)
        paint.textAlign = Paint.Align.CENTER

        val type = Typeface.create("sans-serif-light", Typeface.NORMAL)
        paint.typeface = type

        paint.color = ContextCompat.getColor(context, android.R.color.white)

        canvas.drawText(
            char.toString(),
            size,
            size - ((paint.ascent() + paint.descent()) / 2.0f),
            paint
        )
    }
}
