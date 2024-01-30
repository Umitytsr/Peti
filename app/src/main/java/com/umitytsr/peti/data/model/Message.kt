package com.umitytsr.peti.data.model

import com.google.firebase.Timestamp

data class Message(
    val message : String? = null,
    val senderEmail: String? = null,
    val receiverEmail: String? = null,
    var date: Timestamp? = null
)
