package jp.cordea.mackerelclient

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import com.squareup.picasso.Transformation

class PicassoCircularTransform : Transformation {

    override fun transform(source: Bitmap): Bitmap? {
        val bitmap = Bitmap.createBitmap(source.width, source.width, source.config)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val size = source.width / 2.0f
        canvas.drawCircle(size, size, size, paint)

        source.recycle()
        return bitmap
    }

    override fun key(): String = "circular"
}
