package com.maketicket.qrscaner.ui.model

data class Partaker(
    var id_partaker: Int,
    var id: String,
    var name: String?,
    var lastname: String,
    var birthday: String?,
    var sex: String,
    var email: String?,
    var category:String?,
    var phone:String?,
    var id_system_user:Int,
    var city:String,
    var status:Int,
    var code:String,
    var company:String
)
