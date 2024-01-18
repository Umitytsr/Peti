package com.umitytsr.peti.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class PetModel(
    var petOwnerEmail: String = "",
    var petImage: String = "",
    var petName: String = "",
    var petType: Long = 0,
    var petSex: Long = 0,
    var petGoal: Long = 0,
    var petAge: String = "",
    var petVaccination: Long = 0,
    var petBreed: String = "",
    var petDescription: String = "",
    var date: Timestamp? = null
): Parcelable