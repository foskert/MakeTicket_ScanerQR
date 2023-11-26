package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName
import com.maketicket.qrscaner.ui.model.SpaceMesa

class SpaceMesaResponse(
    @SerializedName("success")  val success: Boolean?,
    @SerializedName("status")   val status: String?,
    @SerializedName("image") val image: String,
    @SerializedName("array") val array: ArrayList<SpaceMesa>

)