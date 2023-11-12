package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName
import com.maketicket.qrscaner.ui.model.Partaker

data class OrderPartakerResponse(
    @SerializedName("success")  val success: Boolean,
    @SerializedName("status")   val status: String,
    @SerializedName("partaker") val partaker: Partaker?,
)