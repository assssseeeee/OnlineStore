package com.example.onlinestore.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import com.example.onlinestore.R
import com.example.onlinestore.firestore.FirestoreClass
import com.example.onlinestore.models.User
import com.example.onlinestore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.editText_password
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        textView_forgotPassword.setOnClickListener(this)
        button_login.setOnClickListener(this)
        textView_register.setOnClickListener(this)
    }


    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(editText_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(editText_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }


    private fun loginRegisteredUser() {
        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email = editText_email.text.toString().trim { it <= ' ' }
            val password = editText_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }


    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()

        if (user.profileCompleted == 0) {
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }


    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.textView_forgotPassword -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.button_login -> {
                    loginRegisteredUser()
                }
                R.id.textView_register -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}