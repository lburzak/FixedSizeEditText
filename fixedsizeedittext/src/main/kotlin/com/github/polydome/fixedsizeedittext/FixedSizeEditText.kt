package com.github.polydome.fixedsizeedittext

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import kotlin.math.roundToInt

class FixedSizeEditText : AppCompatEditText {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context): super(context)

    private val HARDCODED = object {
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 30F
            textAlign = Paint.Align.CENTER
        }

        val preferences = Preferences(
            length = 4,
            spacing = 60,
            boxWidth = 100,
            boxHeight = 200,
            boxBackground = ResourcesCompat
                .getDrawable(resources, R.drawable.background_box, context.theme)
        )
    }

    private val prefs: Preferences = HARDCODED.preferences
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
    }

    private fun drawBoxes(canvas: Canvas) {
        boxBackground?.let {
            boxRect.left = 0
            boxRect.right = (boxRect.left + prefs.boxWidth + 0.5).roundToInt()

            boxRect.top = 30
            boxRect.bottom = (boxRect.top + prefs.boxHeight + 0.5).roundToInt()

            boxBackground.bounds = boxRect
            boxBackground.draw(canvas)
            drawCharacter(canvas, boxRect, 0)

            for (i in characters.indices.drop(1)) {
                boxRect.left = (boxRect.right + prefs.spacing + 0.5).roundToInt()
                boxRect.right = (boxRect.left + prefs.boxWidth + 0.5).roundToInt()

                boxBackground.bounds = boxRect
                boxBackground.draw(canvas)
                drawCharacter(canvas, boxRect, i)
            }
        }
    }

    private fun drawCharacter(canvas: Canvas, boxRect: Rect, charIndex: Int) {
        canvas.drawText(characters, charIndex, 1, boxRect.exactCenterX(), boxRect.exactCenterY(), HARDCODED.textPaint)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        if (isReady() && text != null) {
            text.take(prefs.length)
                .padEnd(prefs.length, '\u0000')
                .forEachIndexed { index, char ->
                    characters[index] = char
                }

            invalidate()
        } else {
            super.onTextChanged(text, start, lengthBefore, lengthAfter)
        }
    }

    /**
     * Determines whether the `super` constructor has finished and variables were initialized
     *
     * SENSELESS_COMPARISON is simply not true, as `onTextChanged` is called before members
     * are initialized
     */
    @Suppress("SENSELESS_COMPARISON")
    private fun isReady(): Boolean = prefs != null
}