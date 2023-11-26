package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName
import com.maketicket.qrscaner.ui.model.MesaSilla
import com.maketicket.qrscaner.ui.model.MesaTabla

class MesaSillaResponse(
    @SerializedName("success")   val success: Boolean?,
    @SerializedName("status")    val status: String?,
    @SerializedName("data")      val data: ArrayList<MesaSilla>?,
)