package com.maketicket.qrscaner.ui.model

import com.google.gson.annotations.SerializedName

data class OrdenEntryResponse(
    @SerializedName("success") var success: Boolean?,
    @SerializedName("status") var status:String?,

)