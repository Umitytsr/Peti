package com.umitytsr.peti.data.repository

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class PetiRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
){

    suspend fun addPet(
        selectedPicture: Uri?, petDescription: String, petName: String, petType: String,
        petSex: String, petGoal: String, petAge: String, petVaccination: String, petBreed: String
    ): Flow<Boolean> = flow {
        val uuid = UUID.randomUUID()
        val reference = storage.reference
        val imageReferance = reference.child("petImages").child("$uuid.jpg")
        try {
            val task = imageReferance.putFile(selectedPicture!!)
            task.await()

            val downloadUrl = imageReferance.downloadUrl.await().toString()

            val petPostMap = hashMapOf<String, Any>()
            petPostMap["owner"] = auth.currentUser!!.email!!
            petPostMap["petImage"] = downloadUrl
            petPostMap["petDescription"] = petDescription
            petPostMap["petName"] = petName
            petPostMap["petType"] = petType
            petPostMap["petSex"] = petSex
            petPostMap["petGoal"] = petGoal
            petPostMap["petAge"] = petAge
            petPostMap["petVaccination"] = petVaccination
            petPostMap["petBreed"] = petBreed
            petPostMap["date"] = Timestamp.now()

            val result = firestore.collection("pets").add(petPostMap).await()
            if (result != null) {
                emit(true)
            } else {
                emit(false)
                Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            emit(false)
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
