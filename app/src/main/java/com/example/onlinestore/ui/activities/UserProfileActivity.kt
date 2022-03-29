package com.example.onlinestore.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.onlinestore.R
import com.example.onlinestore.firestore.FirestoreClass
import com.example.onlinestore.models.User
import com.example.onlinestore.utils.Constants
import com.example.onlinestore.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        editText_first_name_user_profile.setText(mUserDetails.firstName)
        editText_last_name_user_profile.setText(mUserDetails.lastName)
        editText_email_user_profile.isEnabled = false
        editText_email_user_profile.setText(mUserDetails.email)

        if (mUserDetails.profileCompleted == 0) {
            textView_title_user_profile.text = resources.getString(R.string.title_complete_profile)
            editText_first_name_user_profile.isEnabled = false
            editText_last_name_user_profile.isEnabled = false
        } else {
            setupActionBar()
            textView_title_user_profile.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(
                mUserDetails.image,
                imageView_user_photo
            )
            if (mUserDetails.mobile != 0L) {
                editText_mobile_number_user_profile.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                radioButton_male.isChecked
            } else {
                radioButton_female.isChecked
            }
        }

        imageView_user_photo.setOnClickListener(this@UserProfileActivity)
        button_submit_user_profile.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.imageView_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.button_submit_user_profile -> {
                    if (validateUserProfileDetails()) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if (mSelectedImageFileUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val firstName = editText_first_name_user_profile.text.toString().trim { it <= ' ' }
        val lastName = editText_last_name_user_profile.text.toString().trim { it <= ' ' }
        val mobileNumber =
            editText_mobile_number_user_profile.text.toString()
                .trim { it <= ' ' }
        val gender = if (radioButton_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }
        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        userHashMap[Constants.COMPLETE_PROFILE] = 1
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_updating_success), Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this@UserProfileActivity, DashBoardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            imageView_user_photo
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                editText_mobile_number_user_profile.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_mobile_number),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_user_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }
}