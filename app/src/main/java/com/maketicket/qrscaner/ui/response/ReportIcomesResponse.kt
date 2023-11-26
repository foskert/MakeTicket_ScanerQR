package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName
import com.maketicket.qrscaner.ui.model.ReportIcomes

class ReportIcomesResponse (
    @SerializedName("success")          val success: Boolean?,
    @SerializedName("status")           val status: String?,
    @SerializedName("incomesresumen  ") val data: ReportIcomes?,
)