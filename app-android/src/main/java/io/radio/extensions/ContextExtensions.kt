@file:Suppress("NOTHING_TO_INLINE")

package io.radio.shared.base.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.shared.model.ResourceString


inline infix fun Fragment.getAttrColor(@AttrRes attrRes: Int): Int {
    return requireContext().getAttrColor(attrRes)
}

inline infix fun Context.getAttrColor(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    val theme: Theme = theme
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}

inline infix fun Fragment.isPermissionGranted(permission: String): Boolean {
    return requireContext().isPermissionGranted(permission)
}

inline infix fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

inline val Fragment.isLandscapeMode: Boolean
    get() {
        return resources.isLandscapeMode
    }

inline val Context.isLandscapeMode: Boolean
    get() {
        return resources.isLandscapeMode
    }

inline val Resources.isLandscapeMode: Boolean
    get() {
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }


inline infix fun Fragment.parseResourceString(resourceString: ResourceString): String {
    return requireContext() parseResourceString resourceString
}

inline infix fun Context.parseResourceString(resourceString: ResourceString): String {
    return when (resourceString.value) {
        is String -> resourceString.value as String
        is Int -> getString(resourceString.value as Int)
        else -> throw NotImplementedError("Does not support type: ${resourceString.value}")
    }
}