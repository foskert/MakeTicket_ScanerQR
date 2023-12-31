package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName
import com.maketicket.qrscaner.ui.model.Partaker

data class OrderDetailApiResponse(
    @SerializedName("success")             val success: Boolean?,
    @SerializedName("status")              val status: String?,
    @SerializedName("PurchaseOrder")       val purchaseOrder: PurchaseOrder?,
    @SerializedName("Partaker")            val partaker: Partaker?,
    @SerializedName("Articles")            val articles: ArrayList<Articles>?,
    @SerializedName("location")            val location: ArrayList<Location>?,
    @SerializedName("PurchaseOrderTicket") val purchaseOrderTicket: ArrayList<PurchaseOrderTicket>?,
    // @SerializedName("PurchaseOrderTicket") var purchaseOrderTicket: ArrayList<PurchaseOrderTicket>,
)
data class PurchaseOrder(
    var id_system_user: Int,
    val user_login: String?,
    val password: String?,
    val status: Int,
    val password_status: Int,
    val id_system_profile: Int,
    val token: String?,
    val token_date: String?,
    val last_actity: String?,
    val last_ip: String?,
    val created_date: String?,
    val token_password: String?,
    val token_password_date: String?,
    val number_phone: String?,
    val created_by: String?,
    val updated_by: String?,
    val name: String?,
    val lastname: String?,
)
data class Articles(
    var id_purchase_order_article: Int,
    var id_purchase_order: Int,
    var id_article: Int,
    var quantity: String?,
    var amount: String?,
    var tax: String?,
    var total: String?,
    var status: Int,
    var cantidad: Int,
    var neto: String?,
    var iva: String?,
    var imp: String?,
    var web: String?,
    var cotizacion: String?,
    var name:String?,
)
data class PurchaseOrderTicket(
    var id_purchase_order:Int,
    var id_purchase_ticket:Int,
    var status:String?,
    var code:String?,
    var check:Boolean,

    )
data class Location(
    var name:String?
)



