package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName
import com.maketicket.qrscaner.ui.model.SpaceMesa

class SpaceMesaResponse(
    @SerializedName("image") val image: String,
    @SerializedName("array") val array: ArrayList<SpaceMesa>

)