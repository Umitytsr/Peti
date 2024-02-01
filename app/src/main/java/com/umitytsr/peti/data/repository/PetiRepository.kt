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
import com.umitytsr.peti.data.model.ChatCardModel
import com.umitytsr.peti.data.model.ChatModel
import com.umitytsr.peti.data.model.CityModel
import com.umitytsr.peti.data.model.Message
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.data.model.UserModel
import com.umitytsr.peti.util.Const
import com.umitytsr.peti.view.authentication.mainActivity.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PetiRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) {
    suspend fun fetchMyAllMessageWithChatFragment(receiverEmail: String, docPath: String): Flow<List<Message>> {
        val senderEmail = auth.currentUser!!.email
        val senderRoom = senderEmail + receiverEmail
        return fetchMessages(docPath, senderRoom)
    }

    suspend fun fetchMyAllMessageWithInfoFragment(petName: String, petOwnerEmail: String): Flow<List<Message>> {
        val senderEmail = auth.currentUser!!.email
        val senderRoom = senderEmail + petOwnerEmail
        val docPath = petName + petOwnerEmail + senderEmail
        return fetchMessages(docPath, senderRoom)
    }

    private suspend fun fetchMessages(documentPath: String, room: String): Flow<List<Message>> = callbackFlow {
        val listenerRegistration = firestore.collection(Const.MESSAGES).document(documentPath)
            .collection(room)
            .orderBy(Const.DATE, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                }
                val messages = snapshot?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                } ?: emptyList()
                trySend(messages).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    fun sendMessageFromChatFragment(receiverEmail: String, chatDocPath: String, message: String) {
        val senderEmail = auth.currentUser!!.email
        val senderRoom = senderEmail + receiverEmail
        val receiverRoom = receiverEmail + senderEmail
        val messageObject = Message(message, senderEmail, receiverEmail, Timestamp.now())
        if (message.isNotEmpty() && message !=""){
            firestore.collection(Const.MESSAGES).document(chatDocPath).collection(senderRoom)
                .add(messageObject).addOnCompleteListener {
                    firestore.collection(Const.MESSAGES).document(chatDocPath)
                        .collection(receiverRoom).add(messageObject)
                }
        }
    }

    fun sendMessageFromInfoFragment(petName: String, petOwnerEmail: String, message: String) {
        val receiverEmail = petOwnerEmail
        val senderEmail = auth.currentUser!!.email
        val senderRoom = senderEmail + receiverEmail
        val receiverRoom = receiverEmail + senderEmail
        val documentPath = petName + petOwnerEmail + auth.currentUser!!.email
        val messageObject = Message(message, senderEmail, receiverEmail, Timestamp.now())

        if (message.isNotEmpty() && message != ""){
            firestore.collection(Const.MESSAGES).document(documentPath).collection(senderRoom)
                .add(messageObject).addOnCompleteListener {
                    firestore.collection(Const.MESSAGES).document(documentPath).collection(receiverRoom)
                        .add(messageObject)
                }
        }
    }

    suspend fun fetchMyAllChat(): Flow<List<ChatCardModel>> = flow {
        val chatModelList = fetchMyAllChatModel()
        val currentUserEmail = auth.currentUser!!.email
        val chatCardModels = mutableListOf<ChatCardModel>()

        coroutineScope {
            chatModelList.map { chatModel ->
                val receiverEmail = if (chatModel.sender == currentUserEmail) {
                    chatModel.petOwnerEmail
                } else {
                    chatModel.sender
                }

                async {
                    fetchUser(receiverEmail).firstOrNull()?.let { userModel ->
                        ChatCardModel(
                            petName = chatModel.petName,
                            petOwnerEmail = chatModel.petOwnerEmail,
                            receiver = userModel.userFullName,
                            receiverImage = userModel.userImage,
                            receiverEmail = userModel.userEmail,
                            chatDocPath = chatModel.chatDocPath
                        )
                    }
                }
            }.awaitAll().forEach { chatCardModel ->
                chatCardModel?.let { chatCardModels.add(it) }
            }
        }

        emit(chatCardModels)
    }

    private suspend fun fetchMyAllChatModel(): List<ChatModel> {
        val myChatList = mutableListOf<ChatModel>()
        val currentUserEmail = auth.currentUser!!.email

        coroutineScope {
            val deferred1 = async {
                try {
                    firestore.collection(Const.CHATS)
                        .whereEqualTo(Const.PET_OWNER_EMAIL, currentUserEmail)
                        .get()
                        .await()
                        .mapNotNull { it.toObject(ChatModel::class.java) }
                } catch (e: Exception) {
                    emptyList()
                }
            }

            val deferred2 = async {
                try {
                    firestore.collection(Const.CHATS)
                        .whereEqualTo(Const.SENDER, currentUserEmail)
                        .get()
                        .await()
                        .mapNotNull { it.toObject(ChatModel::class.java) }
                } catch (e: Exception) {
                    emptyList()
                }
            }

            myChatList.addAll(deferred1.await())
            myChatList.addAll(deferred2.await())
        }

        return myChatList
    }

    suspend fun createChat(petName: String, petOwnerEmail: String,message: String) {
        val document = firestore.collection(Const.CHATS)
            .whereEqualTo(Const.PET_NAME, petName)
            .whereEqualTo(Const.PET_OWNER_EMAIL, petOwnerEmail)
            .whereEqualTo(Const.SENDER, auth.currentUser!!.email)
            .get()
            .await()
        if (document.isEmpty && message.isNotEmpty() && message != "") {
            val chatDocPath = petName + petOwnerEmail + auth.currentUser!!.email
            val chatModel = ChatModel(petName, petOwnerEmail, auth.currentUser!!.email, chatDocPath)
            firestore.collection(Const.CHATS).add(chatModel).await()
        }
    }

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
                    reference.child(Const.USER_IMAGE).child("${auth.currentUser!!.email}.jpg")
                val task = userImageReference.putFile(selectedPicture)
                task.await()
                downloadUrl = userImageReference.downloadUrl.await().toString()
                userPostMap[Const.USER_IMAGE] = downloadUrl
            }
            userPostMap[Const.USER_FULL_NAME] = userFirstName
            userPostMap[Const.USER_PHONE_NUMBER] = userPhoneNumber


            val document = firestore.collection(Const.USERS)
                .whereEqualTo(Const.USER_EMAIL, "${auth.currentUser!!.email}")
                .get()
                .await()

            if (document != null && !document.isEmpty) {
                val documentId = document.documents[0].id
                firestore.collection(Const.USERS).document(documentId).update(userPostMap).await()
                emit(true)
            }

        } catch (e: Exception) {
            emit(false)
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun fetchUser(petOwnerEmail: String?): Flow<UserModel> = callbackFlow {
        val userEmail = petOwnerEmail ?: auth.currentUser?.email!!
        val subscription = firestore.collection(Const.USERS)
            .whereEqualTo(Const.USER_EMAIL, userEmail)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error) // Hata durumunda Flow'ı kapat
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    var userModel: UserModel? = null
                    for (document in value.documents) {
                        val userEmail = document.get(Const.USER_EMAIL) as String
                        val userFullName = document.get(Const.USER_FULL_NAME) as String
                        val userPhoneNumber = document.get(Const.USER_PHONE_NUMBER) as String
                        val userImage = document.get(Const.USER_IMAGE) as String

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
        userPostMap[Const.USER_EMAIL] = "${auth.currentUser!!.email}"
        userPostMap[Const.USER_FULL_NAME] = userFullName
        userPostMap[Const.USER_PHONE_NUMBER] = ""
        userPostMap[Const.USER_IMAGE] = ""

        firestore.collection(Const.USERS).add(userPostMap).addOnCompleteListener {
            if (!it.isSuccessful) {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun fetchPet(petOwnerEmail: String, petName: String): Flow<PetModel> = callbackFlow {
        val subscription = firestore.collection(Const.PETS)
            .whereEqualTo(Const.PET_OWNER_EMAIL, petOwnerEmail)
            .whereEqualTo(Const.PET_NAME, petName)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error) // Hata durumunda Flow'ı kapat
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    var petModel: PetModel? = null
                    for (document in value.documents) {
                        val petOwnerEmail = document.get(Const.PET_OWNER_EMAIL) as String
                        val petImage = document.get(Const.PET_IMAGE) as String
                        val petName = document.get(Const.PET_NAME) as String
                        val petType = document.get(Const.PET_TYPE) as Long
                        val petSex = document.get(Const.PET_SEX) as Long
                        val petGoal = document.get(Const.PET_GOAL) as Long
                        val petAge = document.get(Const.PET_AGE) as String
                        val petVaccination = document.get(Const.PET_VACCINATION) as Long
                        val petLocation = document.get(Const.PET_LOCATION) as String
                        val petBreed = document.get(Const.PET_BREED) as String
                        val petDescription = document.get(Const.PET_DESCRIPTION) as String
                        val date = document.get(Const.DATE) as Timestamp?

                        petModel = PetModel(
                            petOwnerEmail, petImage, petName, petType, petSex, petGoal, petAge,
                            petVaccination, petLocation ,petBreed, petDescription, date
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
        oldPetPicture: String, newPetPicture: Uri?, petName: String, petType: Long, petSex: Long,
        petGoal: Long, petAge: String, petVaccination: Long, petLocation : String,
        petBreed: String, petDescription: String, date: Timestamp?
    ): Flow<Boolean> = flow {
        try {
            val petPostMap = hashMapOf<String, Any>()
            if (newPetPicture != null) {
                val reference = storage.reference
                val imageReference = reference.child(Const.PET_IMAGE)
                    .child("${auth.currentUser!!.email}")
                    .child("$petName.jpg")
                imageReference.putFile(newPetPicture).await()
                val newPetPictureUrl = imageReference.downloadUrl.await().toString()
                petPostMap[Const.PET_IMAGE] = newPetPictureUrl
            } else {
                petPostMap[Const.PET_IMAGE] = oldPetPicture
            }

            petPostMap[Const.PET_OWNER_EMAIL] = auth.currentUser!!.email!!
            petPostMap[Const.PET_DESCRIPTION] = petDescription
            petPostMap[Const.PET_NAME] = petName
            petPostMap[Const.PET_TYPE] = petType
            petPostMap[Const.PET_SEX] = petSex
            petPostMap[Const.PET_GOAL] = petGoal
            petPostMap[Const.PET_AGE] = petAge
            petPostMap[Const.PET_VACCINATION] = petVaccination
            petPostMap[Const.PET_LOCATION] = petLocation
            petPostMap[Const.PET_BREED] = petBreed
            petPostMap[Const.DATE] = date!!

            val document = firestore.collection(Const.PETS)
                .whereEqualTo(Const.PET_OWNER_EMAIL, "${auth.currentUser!!.email}")
                .whereEqualTo(Const.PET_NAME, petName)
                .get()
                .await()

            if (document != null && !document.isEmpty) {
                val documentId = document.documents[0].id
                firestore.collection(Const.PETS).document(documentId).update(petPostMap).await()
                emit(true)
            }
        } catch (e: Exception) {
            emit(false)
        }
    }

    suspend fun fetchAllCity():Flow<List<CityModel>> = callbackFlow {
        val listenerRegistration = firestore.collection("citys")
            .orderBy("plateCode",Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val cityList = (snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CityModel::class.java)
                } ?: emptyList()).toMutableList()
                trySend(cityList)
            }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun deletePet(petImage: String, petName: String) {
        val querySnapshotFirestore = firestore.collection(Const.PETS)
            .whereEqualTo(Const.PET_IMAGE, petImage)
            .get()
            .await()

        if (!querySnapshotFirestore.isEmpty) {
            for (document in querySnapshotFirestore.documents) {
                firestore.collection(Const.PETS).document(document.id).delete().await()
            }
        }

        val storageRef = storage.reference
        val desertRef = storageRef.child(Const.PET_IMAGE)
            .child("${auth.currentUser!!.email}")
            .child("$petName.jpg")
        desertRef.delete().await()
    }

    suspend fun fetchAllPets(): Flow<List<PetModel>> = callbackFlow {
        val listenerRegistration = firestore.collection(Const.PETS)
            .orderBy(Const.DATE, Query.Direction.DESCENDING)
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
        petAge: String, petVaccination: Long, petLocation: String ,petBreed: String,
        petDescription: String
    ): Flow<Boolean> = flow {
        val reference = storage.reference
        val imageReference = reference.child(Const.PET_IMAGE)
            .child("${auth.currentUser!!.email}")
            .child("$petName.jpg")

        try {
            selectedPetImage?.let {
                imageReference.putFile(it).await()
                val downloadUrl = imageReference.downloadUrl.await().toString()

                val petPostMap = hashMapOf<String, Any>()
                petPostMap[Const.PET_OWNER_EMAIL] = auth.currentUser!!.email!!
                petPostMap[Const.PET_IMAGE] = downloadUrl
                petPostMap[Const.PET_NAME] = petName
                petPostMap[Const.PET_TYPE] = petType
                petPostMap[Const.PET_SEX] = petSex
                petPostMap[Const.PET_GOAL] = petGoal
                petPostMap[Const.PET_AGE] = petAge
                petPostMap[Const.PET_VACCINATION] = petVaccination
                petPostMap[Const.PET_LOCATION] = petLocation
                petPostMap[Const.PET_BREED] = petBreed
                petPostMap[Const.PET_DESCRIPTION] = petDescription
                petPostMap[Const.DATE] = Timestamp.now()

                firestore.collection(Const.PETS).add(petPostMap).await()
                emit(true)
            } ?: emit(false)
        } catch (e: Exception) {
            emit(false)
        }
    }
}