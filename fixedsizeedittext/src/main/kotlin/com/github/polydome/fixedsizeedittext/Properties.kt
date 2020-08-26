package com.github.polydome.fixedsizeedittext

data class Properties(
    var boxWidth: Int,
    var boxHeight: Int,
    var spacing: Int
) {
    companion object {
        internal fun fromPreferences(prefs: Preferences): Properties {
            return Properties(
                boxWidth = prefs.boxWidth,
                boxHeight = prefs.boxHeight,
                spacing = prefs.spacing
            )
        }
    }
}