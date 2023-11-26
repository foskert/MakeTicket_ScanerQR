package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName

data class OrderAdmittedResponse(
    @SerializedName("success")   val success: Boolean?,
    @SerializedName("status")    val status: String?,
    @SerializedName("total")     val total: ArrayList<total>?,
    @SerializedName("funciones") val funciones: ArrayList<funciones>?,
)
data class total(
    var status: String?,
    var name: String?,
    val count: Int,
    val imagen:String?
)
data class funciones(
    var id: Int?,
    var funcion: String?,
    var isSelectd: Boolean = false
)