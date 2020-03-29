package io.radio.shared.base.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources.Theme
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
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

infix fun Fragment.isPermissionGranted(permission: String): Boolean {
    return requireContext().isPermissionGranted(permission)
}

infix fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}