package com.example.dating.bindingadapters

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

class BindingAdapters {

    companion object {
        @BindingAdapter("onNavigationItemSelected")
        @JvmStatic
        fun setOnNavigationItemSelectedListener(
            view: BottomNavigationView,
            listener: BottomNavigationView.OnNavigationItemSelectedListener?
        ) {
            view.setOnNavigationItemSelectedListener(listener)
        }

        @BindingAdapter("imageUrl", "placeholder", "error")
        @JvmStatic
        fun loadImage(view: ImageView, url: Any, placeholder: Drawable, error: Drawable) {
            Glide
                .with(view.context)
                .load(url)
                .placeholder(placeholder)
                .error(error)
                .into(view)
        }

        @BindingAdapter("roundImageUrl", "placeholder", "error")
        @JvmStatic
        fun loadRoundImage(view: ImageView, url: Any, placeholder: Drawable, error: Drawable) {
            Glide
                .with(view.context)
                .load(url)
                .circleCrop()
                .placeholder(placeholder)
                .error(error)
                .into(view)
        }
    }
}