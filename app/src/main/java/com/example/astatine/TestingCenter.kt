package com.example.astatine

import android.os.Build
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.time.LocalDateTime

data class TestingCenter(var name: String?, var description: String?, var address: String?, var lat: Double?, var lng: Double?, var appointments: ArrayList<Appointment>?) : Serializable {
    var key: String = ""

    constructor() : this(null, null, null, null, null, null)
}


class Appointment(var time: String?) : Serializable {
    var isAvailable: Boolean = true
    var appointmentKey: Int = -1
    var patientEmail: String = ""

    constructor() : this(null)

    fun getLocalDateTime(): LocalDateTime {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.parse(time)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
}

class UserAppointment(var time: String, var userEmail: String, var testingCenter: TestingCenter?) {
    fun getLocalDateTime(): LocalDateTime {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.parse(time)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
    constructor():this("","",null)
}