package com.github.polydome.fixedsizeedittext

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import kotlin.math.roundToInt

class FixedSizeEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    private val Hardcoded = object {
        val prefs = Preferences(
            length = 4,
            spacing = 60,
            boxWidth = 100,
            boxHeight = 200,
            boxBackground = ResourcesCompat
                .getDrawable(resources, R.drawable.background_box, context.theme))
    }

    private val prefs = Hardcoded.prefs
    private val characters = CharArray(prefs.length)
    private val boxBackground = prefs.boxBackground

    private val boxRect = Rect(0, 0, 0, 0)

    init {
        background = null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(1000, 1000)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let { drawBoxes(it) }
        super.onDraw(canvas)
    }

    private fun drawBoxes(canvas: Canvas) {
        boxBackground?.let {
            boxRect.left = 0
            boxRect.right = (boxRect.left + prefs.boxWidth + 0.5).roundToInt()

            boxRect.top = 30
            boxRect.bottom = (boxRect.top + prefs.boxHeight + 0.5).roundToInt()

            boxBackground.bounds = boxRect
            boxBackground.draw(canvas)

            for (i in characters.indices.drop(1)) {
                boxRect.left = (boxRect.right + prefs.spacing + 0.5).roundToInt()
                boxRect.right = (boxRect.left + prefs.boxWidth + 0.5).roundToInt()

                boxBackground.bounds = boxRect
                boxBackground.draw(canvas)
            }
        }
    }
}