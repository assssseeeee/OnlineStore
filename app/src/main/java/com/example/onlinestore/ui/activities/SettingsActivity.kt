package com.example.onlinestore.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.onlinestore.R
import com.example.onlinestore.firestore.FirestoreClass
import com.example.onlinestore.models.User

import com.example.onlinestore.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()

        textView_edit_settings.setOnClickListener(this)
        button_logout_settings.setOnClickListener(this)
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
        mUserDetails = user
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

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.button_logout_settings -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.textView_edit_settings -> {

                }
            }
        }
    }
}