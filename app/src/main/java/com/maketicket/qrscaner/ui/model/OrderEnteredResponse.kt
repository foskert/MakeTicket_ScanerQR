package com.maketicket.qrscaner.ui.model

import com.google.gson.annotations.SerializedName

class OrderEnteredResponse (
    @SerializedName("success")   val success: Boolean?,
    @SerializedName("status")    val status: String?,
    @SerializedName("orders")    val orderEntered: ArrayList<OrderEntered>
)