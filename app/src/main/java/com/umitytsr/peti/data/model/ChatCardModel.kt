package com.umitytsr.peti.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatCardModel(
    val petName: String? = null,
    val petOwnerEmail: String? = null,
    val receiver: String? = null,
    val receiverImage: String? = null,
    val receiverEmail: String? = null,
    val chatDocPath: String? = null
): Parcelable
