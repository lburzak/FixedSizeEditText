package com.github.polydome.fixedsizeedittext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.updateBounds
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
    private val props: Properties = Properties.fromPreferences(prefs)
    private val characters = CharArray(prefs.length)

    private val textRect = Rect()

    init {
        background = null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)

        val spacingWidthSum = (prefs.length - 1) * prefs.spacing
        val boxesWidthSum = prefs.boxWidth * prefs.length

        val width = when (widthMeasureMode) {
            MeasureSpec.EXACTLY ->
                MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.AT_MOST ->
                (spacingWidthSum + boxesWidthSum).coerceAtMost(MeasureSpec.getSize(widthMeasureSpec))
            MeasureSpec.UNSPECIFIED ->
                spacingWidthSum + boxesWidthSum
            else -> 0
        }

        val height = when (heightMeasureMode) {
            MeasureSpec.EXACTLY ->
                MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST ->
                prefs.boxHeight.coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
            MeasureSpec.UNSPECIFIED ->
                prefs.boxHeight
            else -> 0
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        props.adjustBoxSize(w, h)
    }

    private fun Properties.adjustBoxSize(maxWidth: Int, maxHeight: Int) {
        val spacingSum = (prefs.length - 1) * prefs.spacing
        val boxesWidthSum = this.boxWidth * prefs.length
        val preferredWidth = spacingSum + boxesWidthSum

        if (preferredWidth > maxWidth) {
            this.spacing = ((this.spacing.toFloat() / preferredWidth * maxWidth)).roundToInt()
            this.boxWidth = ((this.boxWidth.toFloat() / preferredWidth * maxWidth)).roundToInt()
        }

        this.boxHeight = this.boxHeight.coerceAtMost(maxHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let { drawBoxes(it) }
    }

    private fun drawBoxes(canvas: Canvas) {
        prefs.boxBackground?.let {
            it.updateBounds(
                left = 0,
                top = canvas.clipBounds.top,
                right = props.boxWidth,
                bottom = canvas.clipBounds.top + props.boxHeight
            )

            it.draw(canvas)
            drawCharacter(canvas, it.bounds, 0)

            for (i in characters.indices.drop(1)) {
                it.updateBounds(
                    left = it.bounds.right + props.spacing,
                    right = it.bounds.right + props.spacing + props.boxWidth
                )

                it.draw(canvas)
                drawCharacter(canvas, it.bounds, i)
            }
        }
    }

    private fun drawCharacter(canvas: Canvas, boxRect: Rect, charIndex: Int) {
        Gravity.apply(Gravity.CENTER, HARDCODED.textPaint.measureText(characters, charIndex, 1).toInt(), HARDCODED.textPaint.fontMetrics.bottom.toInt(), boxRect, textRect)
        canvas.drawText(characters, charIndex, 1, textRect.exactCenterX(), textRect.exactCenterY(), HARDCODED.textPaint)
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
    private fun isReady(): Boolean = props != null
}