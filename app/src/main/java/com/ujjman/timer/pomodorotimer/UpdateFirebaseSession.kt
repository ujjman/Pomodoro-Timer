package com.ujjman.timer.pomodorotimer


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ujjman.timer.pomodorotimer.data.SessionDetails
import com.ujjman.timer.pomodorotimer.data.Sessions

fun addSession(sessionDetails: SessionDetails) {
    val db = FirebaseFirestore.getInstance()
    var list = ArrayList<SessionDetails>()
    list.add(sessionDetails)
    var sessions = Sessions(list)
    db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
        .collection("sessions").document(sessionDetails.date.toString()).get()
        .addOnSuccessListener { document ->
            try {
                if (document != null) {
                    sessions = document.toObject(Sessions::class.java)!!
                    if (sessions.sessions == null) {
                        sessions.sessions = list
                    } else {
                        sessions.sessions!!.add(sessionDetails)
                    }
                    db.collection("users")
                        .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .collection("sessions").document(sessionDetails.date.toString())
                        .set(sessions)
                }
            } catch (e: Exception) {
                db.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("sessions").document(sessionDetails.date.toString()).set(sessions)
            }
        }
}

