package com.ujjman.timer.pomodorotimer.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ujjman.timer.pomodorotimer.viewmodels.ProfileViewModel

class ProfileViewModelFactory(
    private val db: FirebaseFirestore,
    private val mAuth: FirebaseAuth
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(db, mAuth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}