package com.umitytsr.peti.util

import android.content.Context
import com.umitytsr.peti.R

class Enums {
    enum class PetType(val idType: Long, val stringResourceId: Int) {
        Dog(1, R.string.dog),
        Cat(2, R.string.cat),
        Other(3, R.string.other);

        fun getString(context: Context): String {
            return context.getString(stringResourceId)
        }
    }

    enum class PetSex(val idSex: Long, val stringResourceId: Int) {
        Male(1, R.string.male),
        Female(2, R.string.female);

        fun getString(context: Context): String {
            return context.getString(stringResourceId)
        }
    }

    enum class PetGoal(val idGoal: Long, val stringResourceId: Int) {
        Ownership(1, R.string.ownership),
        Matching(2, R.string.matching);

        fun getString(context: Context): String {
            return context.getString(stringResourceId)
        }
    }

    enum class PetVaccination(val idVac: Long, val stringResourceId: Int) {
        Vaccinated(1, R.string.vaccinated),
        Unvaccinated(2, R.string.unvaccinated);

        fun getString(context: Context): String {
            return context.getString(stringResourceId)
        }
    }

    companion object {
        fun getPetTypeId(id: Long): PetType {
            return PetType.values().find { it.idType == id } ?: PetType.Dog
        }

        fun getPetTypeIdByString(value: String, context: Context): Long {
            return PetType.values().firstOrNull { it.getString(context) == value }?.idType
                ?: PetType.Dog.idType
        }

        fun getPetSexById(id: Long): PetSex {
            return PetSex.values().find { it.idSex == id } ?: PetSex.Male
        }

        fun getPetSexIdByString(value: String, context: Context): Long {
            return PetSex.values().firstOrNull { it.getString(context) == value }?.idSex
                ?: PetSex.Male.idSex
        }

        fun getPetGoalById(id: Long): PetGoal {
            return PetGoal.values().find { it.idGoal == id } ?: PetGoal.Ownership
        }

        fun getPetGoalIdByString(value: String, context: Context): Long {
            return PetGoal.values().firstOrNull { it.getString(context) == value }?.idGoal
                ?: PetGoal.Ownership.idGoal
        }

        fun getPetVaccinationById(id: Long): PetVaccination {
            return PetVaccination.values().find { it.idVac == id } ?: PetVaccination.Vaccinated
        }

        fun getPetVaccinationIdByString(value: String, context: Context): Long {
            return PetVaccination.values().firstOrNull { it.getString(context) == value }?.idVac
                ?: PetVaccination.Vaccinated.idVac
        }
    }
}
