package com.example.ai_language.ui.extensions

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

@BindingAdapter("imageUrl")
fun ImageView.load(url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(this)
            .load(url)
            .into(this)
    }
}

@BindingAdapter("scaleImageUrl")
fun ImageView.scale(url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(this)
            .load(url)
            .transform(RoundedCorners(dpToPx(this.context, 26)))
            .centerCrop()
            .into(this)
    }
}

private fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}



@BindingAdapter("setSrcVolunteerImage")
fun ImageView.setSrcVolunteerImage(id: Int) {
    val bitmap = BitmapFactory.decodeResource(resources, id)
    setImageBitmap(bitmap)
}