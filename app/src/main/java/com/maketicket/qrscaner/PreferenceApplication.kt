package com.maketicket.qrscaner

import android.content.Context

class PreferenceApplication (val context:Context){
    private val QRCODE        = "qr_scaner"
    private val KEY_VALUE     = "key_value"
    private val USER_NAME     = "user_name"
    private val USER_EMAIL    = "user_email"
    private val ID_EVENT      = "id_event"
    private val CODE_QR       = "codeqr"
    private val NAME_EVENT    = "name_event"
    private val ID_ARTICLE    = "id_article"
    private val NAME_ARTICLE  = "name_article"
    private val TYPE_EVENT    = "type_event"
    private val LIST_FUNCTION = "list_function"

    val storage = context.getSharedPreferences(QRCODE, 0)

    fun sesionOut(){
        storage.edit().clear().apply()
        //onBackPressed() para salir de la app
    }

    fun setKeyValue(name:String){
        storage.edit().putString(KEY_VALUE, name.trim()).apply()
    }
    fun getKeyValue():String{
        return storage.getString(KEY_VALUE, "")!!
    }

    fun setUserName(name:String){
        storage.edit().putString(USER_NAME, name).apply()
    }
    fun getUserName():String{
        return storage.getString(USER_NAME, "")!!
    }

    fun setUserEmail(name:String){
        storage.edit().putString(USER_EMAIL, name.trim()).apply()
    }
    fun getUserMail():String{
        return storage.getString(USER_EMAIL, "")!!
    }
    fun setIdEvent(id:Int){
        storage.edit().putInt(ID_EVENT, id).apply()
    }
    fun getIdEvent():Int{
        return storage.getInt(ID_EVENT, 0)
    }
    fun setCodeQR(name:String){
        storage.edit().putString(CODE_QR, name.trim()).apply()
    }
    fun getCodeQR():String{
        return storage.getString(CODE_QR, "")!!
    }
    fun setNameEvent(name:String){
        storage.edit().putString(NAME_EVENT, name).apply()
    }
    fun getNameEvent():String{
        return storage.getString(NAME_EVENT, "EVENTO NO DISPONIBLE")!!
    }
    fun setIdArticle(key:Int){
        storage.edit().putInt(ID_ARTICLE, key).apply()
    }
    fun getIdArticle():Int{
        return storage.getInt(ID_ARTICLE, 0)
    }
    fun setNameArticle(name:String){
        storage.edit().putString(NAME_ARTICLE, name).apply()
    }
    fun getNameArticle():String{
        return storage.getString(NAME_ARTICLE, "FUNCIÓN NO DISPONIBLE")!!
    }
    fun setTypeEvent(name:String){
        storage.edit().putString(TYPE_EVENT, name).apply()
    }
    fun getTypeEvent():String{
        return storage.getString(TYPE_EVENT, "orden")!!
    }
    fun setListFunction(id:MutableList<String>){
        val set: MutableSet<String> = HashSet()
        id.forEach{
            set.add(it)
        }
        storage.edit().putStringSet(LIST_FUNCTION, set).apply()
    }
    fun getListFunction():MutableSet<String>{
        return storage.getStringSet(LIST_FUNCTION, null)!!
    }
}