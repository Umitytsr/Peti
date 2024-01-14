package com.umitytsr.peti.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.data.model.UserModel
import com.umitytsr.peti.view.authentication.mainActivity.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class PetiRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) {

    suspend fun deletePet(petImage: String) {
        val querySnapshot = firestore.collection("pets")
            .whereEqualTo("petImage", petImage)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            for (document in querySnapshot.documents) {
                firestore.collection("pets").document(document.id).delete().await()
            }
        }
    }
    
    fun signOut(requireActivity: Activity){
        auth.signOut()
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        requireActivity.finish()
    }

    fun createUser(userFullName: String) {
        val userPostMap = hashMapOf<String, Any>()
        userPostMap["userEmail"] = "${auth.currentUser!!.email}"
        userPostMap["userFullName"] = userFullName
        userPostMap["userPhoneNumber"] = ""
        userPostMap["userImage"] = ""

        firestore.collection("users").add(userPostMap).addOnCompleteListener {
            if (!it.isSuccessful) {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun fetchUser(petOwnerEmail: String?): Flow<UserModel> = callbackFlow {
        val userEmail = petOwnerEmail ?: auth.currentUser?.email!!
        val subscription = firestore.collection("users")
            .whereEqualTo("userEmail", userEmail)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error) // Hata durumunda Flow'ı kapat
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    var userModel: UserModel? = null
                    for (document in value.documents) {
                        val userEmail = document.get("userEmail") as String
                        val userFullName = document.get("userFullName") as String
                        val userPhoneNumber = document.get("userPhoneNumber") as String
                        val userImage = document.get("userImage") as String

                        userModel = UserModel(userEmail, userFullName, userPhoneNumber, userImage)
                        break
                    }
                    try {
                        // userModel null değilse emit et, null ise null olarak emit et
                        trySend(userModel!!).isSuccess
                    } catch (e: Exception) {
                        // Flow kapatılırken hata alındı
                        close(e)
                    }
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun updateUser(userFirstName:String,userPhoneNumber: String, selectedPicture: Uri?):Flow<Boolean> = flow{
        var downloadUrl = ""
        val userPostMap = hashMapOf<String, Any>()
        try {
            if (selectedPicture != null) {
                val reference = storage.reference
                val userImageReference = reference.child("userImage").child("${auth.currentUser!!.email}.jpg")
                val task = userImageReference.putFile(selectedPicture)
                task.await()
                downloadUrl = userImageReference.downloadUrl.await().toString()
                userPostMap["userImage"] = downloadUrl
            }
            userPostMap["userFullName"] = userFirstName
            userPostMap["userPhoneNumber"] = userPhoneNumber


            val document = firestore.collection("users")
                .whereEqualTo("userEmail", "${auth.currentUser!!.email}")
                .get()
                .await()

            if (document != null && !document.isEmpty) {
                val documentId = document.documents[0].id
                firestore.collection("users").document(documentId).update(userPostMap).await()
                emit(true)
            }

        }catch (e:Exception){
            emit(false)
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun addPet(
        selectedPicture: Uri?, petDescription: String, petName: String, petType: String,
        petSex: String, petGoal: String, petAge: String, petVaccination: String, petBreed: String
    ): Flow<Boolean> = flow {
        val uuid = UUID.randomUUID()
        val reference = storage.reference
        val imageReference = reference.child("petImage").child("$uuid.jpg")

        try {
            selectedPicture?.let {
                imageReference.putFile(it).await()
                val downloadUrl = imageReference.downloadUrl.await().toString()

                val petPostMap = hashMapOf<String, Any>()
                petPostMap["petOwner"] = auth.currentUser!!.email!!
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

                firestore.collection("pets").add(petPostMap).await()
                emit(true)
            } ?: emit(false)
        } catch (e: Exception) {
            emit(false)
        }
    }

    suspend fun fetchAllPets(): Flow<List<PetModel>> = callbackFlow {
        val listenerRegistration = firestore.collection("pets")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                var petList = mutableListOf<PetModel>()
                petList.clear()
                petList = (snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PetModel::class.java)
                } ?: emptyList()).toMutableList()
                trySend(petList).isSuccess
            }

        awaitClose { listenerRegistration.remove() }
    }
}
