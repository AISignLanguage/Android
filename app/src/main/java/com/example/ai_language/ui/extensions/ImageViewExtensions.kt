package com.goorm.kkiri.ui.extensions

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageUrl")
fun ImageView.load(url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(this)
            .load(url)
            .into(this)
    }
}

@BindingAdapter("setSrcVolunteerImage")
fun ImageView.setSrcVolunteerImage(id: Int) {
    val bitmap = BitmapFactory.decodeResource(resources, id)
    setImageBitmap(bitmap)
}