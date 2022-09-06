package com.example.android_storage.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter

object Bindings {

    @JvmStatic
    @BindingAdapter("storageImage")
    fun setStorageImage(imageView: ImageView, image:Int) {
        imageView.setImageResource(image)
    }

}