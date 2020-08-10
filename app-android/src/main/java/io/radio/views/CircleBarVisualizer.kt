package io.radio.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import io.radio.R
import kotlin.math.*


class CircleBarVisualizer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
    }

    private var radius = 0f
    private var points: FloatArray? = null

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleBarVisualizer)
            barPaint.color =
                ta.getColor(R.styleable.CircleBarVisualizer_cbv_barColor, barPaint.color)
            circlePaint.color =
                ta.getColor(R.styleable.CircleBarVisualizer_cbv_circleColor, circlePaint.color)
            ta.recycle()
        }
    }

    fun setBarColor(@ColorInt color: Int) {
        barPaint.color = color
        invalidate()
    }

    fun setCircleColor(@ColorInt color: Int) {
        barPaint.color = color
        invalidate()
    }

    fun setBytes(bytes: ByteArray) {
        if (bytes.isEmpty()) {
            return
        }
        val points = FloatArray(bytes.size)
        val width = width
        val height = height
        var angle = 0.0
        var i = 0
        while (i < 120) {
            val x = ceil(i * 8.5).toInt()

            val t = (-abs(bytes[x] + 128)).toByte() * (height / 4) / 128

            points[i * 4] = (width / 2 + radius * cos(Math.toRadians(angle))).toFloat()
            points[i * 4 + 1] =
                (height / 2 + radius * sin(Math.toRadians(angle))).toFloat()
            points[i * 4 + 2] =
                (width / 2 + (radius + t) * cos(Math.toRadians(angle))).toFloat()
            points[i * 4 + 3] =
                (height / 2 + (radius + t) * sin(Math.toRadians(angle))).toFloat()
            i++
            angle += 3f
        }

        this.points = points

        if (Looper.getMainLooper().thread === Thread.currentThread()) {
            invalidate()
        } else {
            postInvalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.radius = (max(w, h) * 0.65 / 2).toFloat()
        val circumference = 2 * Math.PI * radius
        barPaint.strokeWidth = (circumference / 120).toFloat()
        circlePaint.strokeMiter = 4f //todo change this
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(width / 2f, height / 2f, radius, circlePaint)
        points?.let {
            canvas.drawLines(it, barPaint)
        }
    }

}