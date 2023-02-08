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
import kotlin.collections.HashMap

class StatisticsViewModel(
    private val db: FirebaseFirestore, private val mAuth: FirebaseAuth
) : ViewModel() {

    var listOfPast7DaysSessions = ArrayList<ArrayList<SessionDetails>>()
    var listOfAllSessions = ArrayList<SessionDetails>()
    var noOfSessionsPerDayMap = HashMap<String?, Int?>()
    var avgDurationOfSessionsPerDayMap = HashMap<String?, Float?>()
    var checkData = MutableLiveData(false)
    var checkDataIsNull = false
    var checkWhichDataToShow = MutableLiveData(0)
    var noOfDaysInData = 0
    var checkForData = 0
    var isAllSessionsDataLoaded = MutableLiveData(false)


    fun clear() {
        checkDataIsNull = false
        isAllSessionsDataLoaded = MutableLiveData(false)
        checkForData = 0
        listOfAllSessions = ArrayList<SessionDetails>()
        checkWhichDataToShow = MutableLiveData(0)
        listOfPast7DaysSessions = ArrayList<ArrayList<SessionDetails>>()
        noOfSessionsPerDayMap = HashMap<String?, Int?>()
        avgDurationOfSessionsPerDayMap = HashMap<String?, Float?>()
        checkData = MutableLiveData(false)
        noOfDaysInData = 0
    }

    fun getLastSevenDaysData() {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val current = formatter.format(date)
        val myDate: Date = formatter.parse(current)
        val listOfPast7Dates = ArrayList<String>()

        var i = 0
        while (i < 7) {
            val calendar = Calendar.getInstance()
            calendar.time = myDate
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val newDate = calendar.time
            val date = formatter.format(newDate)
            listOfPast7Dates.add(date)
            i++
        }
        getListOfSessions(listOfPast7Dates)
    }

    private fun getListOfSessions(listOfPast7Dates: ArrayList<String>) {
        db.collection("users").document(mAuth.currentUser?.uid.toString()).collection("sessions")
            .get().addOnSuccessListener { documents ->
                try {
                    if (documents != null && documents.size() > 0) {
                        for (document in documents) {
                            if (listOfPast7Dates.contains(document.id.trim())) {
                                noOfDaysInData++
                            }
                        }
                        for (document in documents) {
                            if (listOfPast7Dates.contains(document.id.trim())) {
                                extractSessionDetailsFromFirestore(document.id)
                            }
                        }

                    } else {
                        checkDataIsNull = true
                        checkData.value = true

                    }
                } catch (e: Exception) {
                }
            }.addOnFailureListener {}

    }


    private fun extractSessionDetailsFromFirestore(date: String) {
        var list: ArrayList<SessionDetails>
        var sessions: Sessions
        db.collection("users").document(mAuth.currentUser?.uid.toString()).collection("sessions")
            .document(date).get().addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        sessions = document.toObject(Sessions::class.java)!!
                        if (sessions.sessions != null) {
                            list = sessions.sessions!!
                            listOfPast7DaysSessions.add(list)
                        }
                    }
                    noOfDaysInData--
                    if (noOfDaysInData == 0) {
                        checkData.value = true
                    }

                } catch (e: Exception) {
                }

            }.addOnFailureListener {}.addOnFailureListener {}
    }

    fun getNoOfSessionsPerDay() {
        for (list in listOfPast7DaysSessions) {
            noOfSessionsPerDayMap[list[0].date] = list.size
        }
    }

    fun getAvgDurationOfSessionPerDay() {
        var duration = 0
        var avgDuration = 0f
        for (list in listOfPast7DaysSessions) {
            for (sessionDetails in list) {
                duration += sessionDetails.length!!
            }
            avgDuration = ((duration * 1.0f) / (list.size * 1.0f))
            avgDurationOfSessionsPerDayMap[list[0].date] = avgDuration
            duration = 0
        }
    }
}