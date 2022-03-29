package com.example.onlinestore.ui.activities

import android.os.Bundle
import com.example.onlinestore.R
import com.example.onlinestore.firestore.FirestoreClass
import com.example.onlinestore.models.User

import com.example.onlinestore.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_settings)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_arrow_back_24)
            actionBar.title = null
        }
        toolbar_settings.setNavigationOnClickListener { onBackPressed() }
    }


    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User) {
        hideProgressDialog()
        GlideLoader(this@SettingsActivity).loadUserPicture(
            user.image,
            imageView_user_photo_settings
        )
        textView_name_settings.text = "${user.firstName} ${user.lastName}"
        textView_email_settings.text = user.email
        textView_gender_settings.text = user.gender
        textView_mobileNumber_settings.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
}