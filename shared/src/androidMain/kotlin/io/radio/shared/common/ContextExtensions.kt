package io.radio.shared.common

import android.content.Context
import android.content.res.Resources.Theme
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment


infix fun Fragment.getAttrColor(@AttrRes attrRes: Int): Int {
    return requireContext().getAttrColor(attrRes)
}

infix fun Context.getAttrColor(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    val theme: Theme = theme
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}