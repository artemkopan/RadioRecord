package io.radio.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import io.radio.R


@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    //todo add support dark theme
    val colors = lightColors(
//        primary = colorResource(id = R.color.colorPrimary),
//        primaryVariant = colorResource(id = R.color.colorAccent),
//        secondary = colorResource(id = R.color.colorPrimaryDark)
        primary = colorResource(id = R.color.colorAccent),
        primaryVariant = colorResource(id = R.color.colorAccent),
        secondary = colorResource(id = R.color.colorAccent)
    )

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}