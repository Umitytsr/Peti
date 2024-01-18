package com.umitytsr.peti.util

import android.content.Context
import com.umitytsr.peti.R

class Enums {
    enum class PetType(val idType: Long, val stringResourceId: Int): EnumInterface {
        Dog(1, R.string.dog),
        Cat(2, R.string.cat),
        Other(3, R.string.other);

        override fun getId() = idType
        override fun getString(context: Context) = context.getString(stringResourceId)
    }

    enum class PetSex(val idSex: Long, val stringResourceId: Int): EnumInterface {
        Male(1, R.string.male),
        Female(2, R.string.female);

        override fun getId() = idSex
        override fun getString(context: Context) = context.getString(stringResourceId)
    }

    enum class PetGoal(val idGoal: Long, val stringResourceId: Int): EnumInterface {
        Ownership(1, R.string.ownership),
        Matching(2, R.string.matching);

        override fun getId() = idGoal
        override fun getString(context: Context) = context.getString(stringResourceId)
    }

    enum class PetVaccination(val idVac: Long, val stringResourceId: Int): EnumInterface {
        Vaccinated(1, R.string.vaccinated),
        Unvaccinated(2, R.string.unvaccinated);

        override fun getId() = idVac
        override fun getString(context: Context) = context.getString(stringResourceId)
    }
}

interface EnumInterface {
    fun getId(): Long
    fun getString(context: Context): String
}

inline fun <reified T : Enum<T>> getStringForEnumById(enumId: Long, context: Context): String
        where T : EnumInterface {
    return enumValues<T>().firstOrNull { it.getId() == enumId }?.getString(context) ?: ""
}

inline fun <reified T : Enum<T>> getIdForEnumString(enumString: String, context: Context): Long
        where T : EnumInterface {
    return enumValues<T>().firstOrNull { it.getString(context) == enumString }?.getId() ?: -1
}
