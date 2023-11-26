package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName
import com.maketicket.qrscaner.ui.model.ReportVentas
import com.maketicket.qrscaner.ui.model.TicketByFunction

class ReportVentasResponse (
    @SerializedName("success")   val success: Boolean?,
    @SerializedName("status")    val status: String?,
    @SerializedName("aforo")     val data: ReportVentas?,
)