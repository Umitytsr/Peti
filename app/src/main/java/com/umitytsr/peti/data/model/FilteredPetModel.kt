package com.umitytsr.peti.data.model

class FilteredPetModel(
    val selectedPetType: Long? = null,
    val selectedPetSex: Long? = null,
    val selectedPetGoal: Long? = null,
    val selectedPetAge: String? = null,
    val selectedPetVac: Long? = null,
    val selectedPetLocation : String? = null
)