package com.umitytsr.peti.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class PetModel(
    var petOwnerEmail: String? = null,
    var petImage: String? = null,
    var petName: String? = null,
    var petType: Long? = null,
    var petSex: Long? = null,
    var petGoal: Long? = null,
    var petAge: String? = null,
    var petVaccination: Long? = null,
    var petBreed: String? = null,
    var petDescription: String? = null,
    var date: Timestamp? = null
): Parcelable