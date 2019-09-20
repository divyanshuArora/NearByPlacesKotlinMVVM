package com.example.nearbyplaceskotlinmvvm.service

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object Utils
{
    @BindingAdapter("getImageFromUrl")
    @JvmStatic
    fun getImageUrl(imageView: ImageView, url: String)
    {
        Glide.with(imageView.context).load(url).into(imageView)
    }
}