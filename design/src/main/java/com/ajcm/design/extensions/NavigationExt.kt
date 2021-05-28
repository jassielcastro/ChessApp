package com.ajcm.design.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.navigateTo(@IdRes idAction: Int, bundle: Bundle? = null) {
    findNavController().navigate(idAction, bundle)
}
