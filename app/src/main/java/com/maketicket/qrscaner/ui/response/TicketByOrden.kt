package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName
import com.maketicket.qrscaner.ui.model.TicketByFunction

data class TicketByOrden (
        @SerializedName("success")   val success: Boolean?,
        @SerializedName("status")    val status: String?,
        @SerializedName("data")      val data: ArrayList<TicketByFunction>?,
    )
