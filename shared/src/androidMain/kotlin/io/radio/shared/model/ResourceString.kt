@file:Suppress("NOTHING_TO_INLINE")

package io.radio.shared.model

import android.content.Context
import androidx.fragment.app.Fragment

actual inline class ResourceString(val value: Any)


inline infix fun Fragment.parseResourceString(resourceString: ResourceString): String {
    return requireContext() parseResourceString resourceString
}

inline infix fun Context.parseResourceString(resourceString: ResourceString): String {
    return when (resourceString.value) {
        is String -> resourceString.value
        is Int -> getString(resourceString.value)
        else -> throw NotImplementedError("Does not support type: ${resourceString.value}")
    }
}