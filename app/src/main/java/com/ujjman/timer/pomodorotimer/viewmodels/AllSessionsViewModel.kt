package com.ujjman.timer.pomodorotimer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ujjman.timer.pomodorotimer.data.SessionDetails
import com.ujjman.timer.pomodorotimer.data.Sessions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AllSessionsViewModel(
    private val db: FirebaseFirestore, private val mAuth: FirebaseAuth
) : ViewModel() {

    var checkForData = 0
    var isAllSessionsDataLoaded = MutableLiveData(false)
    var listOfAllSessions = ArrayList<SessionDetails>()
    var listOfLastWeekSessions = ArrayList<SessionDetails>()
    var listOfLastMonthSessions = ArrayList<SessionDetails>()
    var listOfTodaySessions = ArrayList<SessionDetails>()
    var checkDataIsNull = false

    fun clear() {
        checkForData = 0
        checkDataIsNull = false
        isAllSessionsDataLoaded = MutableLiveData(false)
        listOfAllSessions = ArrayList<SessionDetails>()
        listOfLastWeekSessions = ArrayList<SessionDetails>()
        listOfLastMonthSessions = ArrayList<SessionDetails>()
        listOfTodaySessions = ArrayList<SessionDetails>()
    }

    fun getAllSessionsData() {
        db.collection("users").document(mAuth.currentUser?.uid.toString()).collection("sessions")
            .get().addOnSuccessListener { documents ->
                try {
                    if (documents != null && documents.size() > 0) {
                        checkForData = documents.size()
                        for (document in documents) {
                            getSessionDetailsFromDate(document.id)
                        }
                    } else {
                        checkDataIsNull = true
                        isAllSessionsDataLoaded.value = true
                    }
                } catch (e: Exception) {
                }
            }

    }

    private fun getSessionDetailsFromDate(date: String) {
        var list: ArrayList<SessionDetails>
        var sessions: Sessions
        db.collection("users").document(mAuth.currentUser?.uid.toString()).collection("sessions")
            .document(date).get().addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        sessions = document.toObject(Sessions::class.java)!!
                        if (sessions.sessions != null) {
                            list = sessions.sessions!!
                            for (sessionDetails in list) {
                                listOfAllSessions.add(sessionDetails)
                            }
                            checkForData--
                            if (checkForData == 0) {
                                sortByLastMonth()
                                sortByToday()
                                sortByLastWeek()
                                isAllSessionsDataLoaded.value = true
                            }
                        }
                    }

                } catch (e: Exception) {

                }

            }.addOnFailureListener {

            }
    }


    private fun minusXNoOfDaysFromToday(x: Int): ArrayList<String> {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val current = formatter.format(date)
        val myDate: Date = formatter.parse(current)
        val listOfPastXDates = ArrayList<String>()
        var i = 0
        while (i < x) {
            val calendar = Calendar.getInstance()
            calendar.time = myDate
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val newDate = calendar.time
            val date = formatter.format(newDate)
            listOfPastXDates.add(date)
            i++
        }
        return listOfPastXDates
    }

    private fun sortByLastWeek() {

        val listOfPast7Dates = minusXNoOfDaysFromToday(7)
        for (sessionDetails in listOfAllSessions) {
            if (listOfPast7Dates.contains(sessionDetails.date?.trim())) {
                listOfLastWeekSessions.add(sessionDetails)
            }
        }
    }

    private fun sortByLastMonth() {
        val listOfPast30Dates = minusXNoOfDaysFromToday(30)
        for (sessionDetails in listOfAllSessions) {
            if (listOfPast30Dates.contains(sessionDetails.date?.trim())) {
                listOfLastMonthSessions.add(sessionDetails)
            }
        }
    }

    private fun sortByToday() {
        val listOfToday = minusXNoOfDaysFromToday(1)
        for (sessionDetails in listOfAllSessions) {
            if (listOfToday.contains(sessionDetails.date?.trim())) {
                listOfTodaySessions.add(sessionDetails)
            }
        }
    }

}