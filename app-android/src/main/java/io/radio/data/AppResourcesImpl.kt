package io.radio.data

import android.content.Context
import androidx.appcompat.view.ContextThemeWrapper
import io.radio.R
import io.radio.extensions.getAttrColor
import io.shared.resources.AppResources

class AppResourcesImpl(private val context: Context) :
    AppResources {

    private val themedContext by lazy {
        ContextThemeWrapper(context, R.style.AppTheme)
    }

    override val accentColor: Int
        get() = themedContext.getAttrColor(R.attr.colorAccent)

    override val primaryColor: Int
        get() = themedContext.getAttrColor(R.attr.colorPrimary)

}