package com.example.onlinestore.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import java.io.IOException
import java.net.URI

class GlideLoader(val context: Context) {

    fun loadUserPicture(imageURI: Uri, imageView: ImageView) {

        try {
            Glide.with(context)
                .load(imageURI)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}