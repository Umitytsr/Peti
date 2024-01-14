package com.umitytsr.peti.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class PetModel(
    var petOwner: String = "",
    var petImage: String = "",
    var petDescription: String = "",
    var petName: String = "",
    var petType: String = "",
    var petSex: String = "",
    var petGoal: String = "",
    var petAge: String = "",
    var petVaccination: String = "",
    var petBreed: String = "",
    var date: Timestamp? = null
): Parcelable