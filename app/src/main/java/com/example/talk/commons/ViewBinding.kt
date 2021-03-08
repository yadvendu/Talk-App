package com.example.talk.commons

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleorgone")
fun setVisibility(progressBar: ProgressBar, visible: Boolean) {
    with(progressBar) {
        visibility = if (visible)
            View.VISIBLE
        else
            View.GONE
    }
}