package com.ujjman.timer.pomodorotimer.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ujjman.timer.pomodorotimer.R
import com.ujjman.timer.pomodorotimer.data.UserData

class LoginViewModel(
    private val db: FirebaseFirestore, private val mAuth: FirebaseAuth
) : ViewModel() {

    fun setInitialDataToFirestore(context: Context) {
        var userData =
            UserData(mAuth.currentUser?.displayName, context.getString(R.string.no_photo_url), null)
        db.collection("users").document(mAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        userData = document.toObject(UserData::class.java)!!
                        if (userData.username.equals("", true)) {
                            userData = UserData(
                                mAuth.currentUser?.displayName,
                                context.getString(R.string.no_photo_url),
                                null
                            )
                        }
                        db.collection("users").document(mAuth.currentUser?.uid.toString())
                            .set(userData)
                    }

                } catch (e: Exception) {
                    db.collection("users").document(mAuth.currentUser?.uid.toString()).set(userData)
                }
            }.addOnFailureListener {

            }

    }

}
