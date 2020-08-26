package com.github.polydome.fixedsizeedittext

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getIntegerOrThrow

internal data class Preferences(
    val length: Int,
    val spacing: Int,
    val boxWidth: Int,
    val boxHeight: Int,
    val boxBackground: Drawable?,
    val boxGravity: Int
) {
    companion object {
        fun fromAttributes(context: Context, attrs: AttributeSet): Preferences {
            val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.FixedSizeEditText, 0, 0)

            val prefs = Preferences(
                length = styledAttributes.getIntegerOrThrow(R.styleable.FixedSizeEditText_inputLength),
                spacing = styledAttributes.getDimensionPixelSize(R.styleable.FixedSizeEditText_spacing, 0),
                boxHeight = styledAttributes.getDimensionPixelSize(R.styleable.FixedSizeEditText_boxHeight, 0),
                boxWidth = styledAttributes.getDimensionPixelSize(R.styleable.FixedSizeEditText_boxWidth, 0),
                boxGravity = styledAttributes.getInt(R.styleable.FixedSizeEditText_boxGravity, Gravity.CENTER),
                boxBackground = styledAttributes.getDrawable(R.styleable.FixedSizeEditText_boxBackground)
            )

            styledAttributes.recycle()
            return prefs
        }

        fun default(context: Context): Preferences =
            Preferences(
                length = 4,
                spacing = 60,
                boxWidth = 100,
                boxHeight = 200,
                boxBackground = ResourcesCompat
                    .getDrawable(context.resources, R.drawable.background_dash, context.theme),
                boxGravity = Gravity.CENTER
            )
    }
}