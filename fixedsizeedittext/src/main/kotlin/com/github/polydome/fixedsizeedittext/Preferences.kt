package com.github.polydome.fixedsizeedittext

import android.graphics.drawable.Drawable

internal data class Preferences(
    val length: Int,
    val spacing: Int,
    val boxWidth: Int,
    val boxHeight: Int,
    val boxBackground: Drawable?,
    val boxGravity: Int
)