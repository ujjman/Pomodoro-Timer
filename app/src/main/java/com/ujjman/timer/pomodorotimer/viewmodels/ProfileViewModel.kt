package com.ujjman.timer.pomodorotimer.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.ujjman.timer.pomodorotimer.data.UserData
import kotlinx.coroutines.tasks.await

class ProfileViewModel(
    private val db: FirebaseFirestore,
    private val mAuth: FirebaseAuth
) : ViewModel() {
    private val _isDarkTheme = MutableLiveData<Boolean>()
    val photoUrl = MutableLiveData<String>()
    var username = MutableLiveData<String>()
    val isDarkTheme: LiveData<Boolean>
        get() = _isDarkTheme

    fun setDarkTheme() {
        _isDarkTheme.value = true
    }

    fun setLightTheme() {
        _isDarkTheme.value = false
    }

    fun putPhotoUrlToFirestore(url: String) {
        var userData: UserData
        db.collection("users").document(mAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        userData = document.toObject(UserData::class.java)!!
                        userData.photoUrl = url
                        db.collection("users").document(mAuth.currentUser?.uid.toString())
                            .set(userData)
                    }
                } catch (e: Exception) {
                }
            }
    }

    fun getPhotoUrl(context: Context) {

        var userData: UserData
        db.collection("users").document(mAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        userData = document.toObject(UserData::class.java)!!
                        photoUrl.value = userData.photoUrl
                        username.value = userData.username.toString()
                    }
                } catch (ex: Exception) {
                }
            }

    }

    fun saveName(name: String) {
        var userData: UserData
        db.collection("users").document(mAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        userData = document.toObject(UserData::class.java)!!
                        userData.username = name
                        username.value = name
                        db.collection("users").document(mAuth.currentUser?.uid.toString())
                            .set(userData)
                    }
                } catch (ex: Exception) {
                }
            }
    }

    suspend fun uploadPhoto(
        uri: Uri,
        name: String,
        mimeType: String?,
        callback: (url: String) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val fileRef = storageRef.child("images/$name")

        val metadata = mimeType?.let {
            StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()
        }

        if (metadata != null) {
            fileRef.putFile(uri, metadata).await()
        } else {
            fileRef.putFile(uri).await()
        }

        callback(fileRef.downloadUrl.await().toString())
    }


}