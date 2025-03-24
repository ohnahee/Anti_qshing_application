package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class HighlightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#80000000") // 반투명 검은색 배경
        style = Paint.Style.FILL
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    var highlightRect: RectF? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        highlightRect?.let {
            canvas.drawRect(it, clearPaint) // 특정 영역을 투명하게 처리
        }
    }

    fun setHighlightArea(x: Float, y: Float, width: Float, height: Float) {
        highlightRect = RectF(x, y, x + width, y + height)
        invalidate()
    }
}
