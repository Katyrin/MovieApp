package com.katyrin.movieapp.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.katyrin.movieapp.R

fun View.createAndShow(text: String, actionText: String = "",
                               action: ((View) -> Unit)? = null,
                               length: Int = Snackbar.LENGTH_INDEFINITE) {
    Snackbar.make(this, text, length).also {
        if (action != null) it.setAction(actionText, action)
    }.apply {
        anchorView = this@createAndShow.context.getActivity()?.findViewById(R.id.bottomNavigation)
    }.show()
}

private fun Context.getActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.getActivity()
        else -> null
    }
}