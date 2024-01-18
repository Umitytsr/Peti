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
import java.io.File
import javax.inject.Inject

class PetiRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) {

    fun signOut(requireActivity: Activity) {
        auth.signOut()
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        requireActivity.finish()
    }

    suspend fun updateUser(
        userFirstName: String,
        userPhoneNumber: String,
        selectedPicture: Uri?
    ): Flow<Boolean> = flow {
        var downloadUrl = ""
        val userPostMap = hashMapOf<String, Any>()
        try {
            if (selectedPicture != null) {
                val reference = storage.reference
                val userImageReference =
                    reference.child("userImage").child("${auth.currentUser!!.email}.jpg")
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

        } catch (e: Exception) {
            emit(false)
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
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

    suspend fun fetchPet(petOwnerEmail: String, petName: String): Flow<PetModel> = callbackFlow {
        val subscription = firestore.collection("pets")
            .whereEqualTo("petOwnerEmail", petOwnerEmail)
            .whereEqualTo("petName", petName)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error) // Hata durumunda Flow'ı kapat
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    var petModel: PetModel? = null
                    for (document in value.documents) {
                        val petOwnerEmail = document.get("petOwnerEmail") as String
                        val petImage = document.get("petImage") as String
                        val petName = document.get("petName") as String
                        val petType = document.get("petType") as Long
                        val petSex = document.get("petSex") as Long
                        val petGoal = document.get("petGoal") as Long
                        val petAge = document.get("petAge") as String
                        val petVaccination = document.get("petVaccination") as Long
                        val petBreed = document.get("petBreed") as String
                        val petDescription = document.get("petDescription") as String
                        val date = document.get("date") as Timestamp?

                        petModel = PetModel(
                            petOwnerEmail, petImage, petName, petType, petSex,
                            petGoal, petAge, petVaccination, petBreed, petDescription, date
                        )
                        break
                    }
                    try {
                        trySend(petModel!!).isSuccess
                    } catch (e: Exception) {
                        close(e)
                    }
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun updatePet(
        oldPetPicture: String, newPetPicture: Uri?, oldPetName: String, newPetName: String,
        petType: Long, petSex: Long, petGoal: Long, petAge: String, petVaccination: Long,
        petBreed: String, petDescription: String, date: Timestamp?
    ): Flow<Boolean> = flow {
        try {
            val petPostMap = hashMapOf<String, Any>()
            if (newPetPicture != null) {
                val reference = storage.reference
                val imageReference = reference.child("petImage")
                    .child("${auth.currentUser!!.email}")
                    .child("$oldPetName.jpg")
                imageReference.putFile(newPetPicture).await()
                val newPetPictureUrl = imageReference.downloadUrl.await().toString()
                petPostMap["petImage"] = newPetPictureUrl
            } else {
                petPostMap["petImage"] = oldPetPicture
            }

            if (oldPetName != newPetName) {
                val storageRef = storage.reference

                val existingFileRef = storageRef.child("petImage")
                    .child("${auth.currentUser!!.email}").child("$oldPetName.jpg")

                val newFileRef = storageRef.child("petImage")
                    .child("${auth.currentUser!!.email}").child("$newPetName.jpg")

                // Eski dosyayı geçici bir dosya olarak indiriyor
                val localFile = File.createTempFile("temp", "jpg")
                existingFileRef.getFile(localFile).await()

                // Yeni isimle Firebase Storage'a yükledik
                newFileRef.putFile(Uri.fromFile(localFile)).await()

                // Eski dosyayı sildik
                existingFileRef.delete().await()
            }

            petPostMap["petOwnerEmail"] = auth.currentUser!!.email!!
            petPostMap["petDescription"] = petDescription
            petPostMap["petName"] = newPetName
            petPostMap["petType"] = petType
            petPostMap["petSex"] = petSex
            petPostMap["petGoal"] = petGoal
            petPostMap["petAge"] = petAge
            petPostMap["petVaccination"] = petVaccination
            petPostMap["petBreed"] = petBreed
            petPostMap["date"] = date!!

            val document = firestore.collection("pets")
                .whereEqualTo("petOwnerEmail", "${auth.currentUser!!.email}")
                .whereEqualTo("petName", oldPetName)
                .get()
                .await()

            if (document != null && !document.isEmpty) {
                val documentId = document.documents[0].id
                firestore.collection("pets").document(documentId).update(petPostMap).await()
                emit(true)
            }
        } catch (e: Exception) {
            emit(false)
        }
    }

    suspend fun deletePet(petImage: String, petName: String) {
        val querySnapshotFirestore = firestore.collection("pets")
            .whereEqualTo("petImage", petImage)
            .get()
            .await()

        if (!querySnapshotFirestore.isEmpty) {
            for (document in querySnapshotFirestore.documents) {
                firestore.collection("pets").document(document.id).delete().await()
            }
        }

        val storageRef = storage.reference
        val desertRef = storageRef.child("petImage")
            .child("${auth.currentUser!!.email}")
            .child("$petName.jpg")
        desertRef.delete().await()
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

    suspend fun addPet(
        selectedPetImage: Uri?, petName: String, petType: Long, petSex: Long, petGoal: Long,
        petAge: String, petVaccination: Long, petBreed: String, petDescription: String
    ): Flow<Boolean> = flow {
        val reference = storage.reference
        val imageReference = reference.child("petImage")
            .child("${auth.currentUser!!.email}")
            .child("$petName.jpg")

        try {
            selectedPetImage?.let {
                imageReference.putFile(it).await()
                val downloadUrl = imageReference.downloadUrl.await().toString()

                val petPostMap = hashMapOf<String, Any>()
                petPostMap["petOwnerEmail"] = auth.currentUser!!.email!!
                petPostMap["petImage"] = downloadUrl
                petPostMap["petName"] = petName
                petPostMap["petType"] = petType
                petPostMap["petSex"] = petSex
                petPostMap["petGoal"] = petGoal
                petPostMap["petAge"] = petAge
                petPostMap["petVaccination"] = petVaccination
                petPostMap["petBreed"] = petBreed
                petPostMap["petDescription"] = petDescription
                petPostMap["date"] = Timestamp.now()

                firestore.collection("pets").add(petPostMap).await()
                emit(true)
            } ?: emit(false)
        } catch (e: Exception) {
            emit(false)
        }
    }
}
