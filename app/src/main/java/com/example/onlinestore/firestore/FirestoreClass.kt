package com.example.onlinestore.firestore

import android.app.Activity
import android.util.Log
import com.example.onlinestore.activities.LoginActivity
import com.example.onlinestore.activities.RegisterActivity
import com.example.onlinestore.models.User
import com.example.onlinestore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()


    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFirestore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegistrationSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName, "error while register with user", e
                )
            }
    }


    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }


    fun getUserDetails(activity: Activity) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName.toString(), document.toString())

                val user = document.toObject(User::class.java)!!

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }
            }.addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName.toString(), "error")
            }
    }
}