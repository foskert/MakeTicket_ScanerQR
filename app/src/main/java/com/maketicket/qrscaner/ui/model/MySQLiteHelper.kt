package com.maketicket.qrscaner.ui.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.maketicket.qrscaner.QRSanerApplication.Companion.preference
import com.maketicket.qrscaner.ui.response.Articles
import com.maketicket.qrscaner.ui.response.Location
import com.maketicket.qrscaner.ui.response.PurchaseOrderTicket
import java.text.SimpleDateFormat
import java.util.*

class MySQLiteHelper(context: Context):SQLiteOpenHelper(
    context, "make_tickets.db", null, 6) {
    private var dogCode = mutableListOf<Ticket>()
    private val dogPartaker = mutableListOf<Partaker>()
    private val dogFunction= mutableListOf<Function>()

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("MODELHELPER SQLiteDatabase", "crando")
        val ticketCreate = "CREATE TABLE tickets (id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT, status TEXT, token TEXT, date TEXT, email TEXT,  id_event INTEGER DEFAULT 0 ) "
        db!!.execSQL(ticketCreate)
        val partakerCreate = " CREATE TABLE partakers (id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT, dni TEXT, name TEXT, last_name TEXT, email TEXT, phone TEXT, photo_url TEXT , date TEXT, id_user TEXT)"
        db.execSQL(partakerCreate)
        val articleCreate = "CREATE TABLE articles (id INTEGER PRIMARY KEY AUTOINCREMENT, partaker_id INTEGER, quantity TEXT,  amount TEXT, tax TEXT, total TEXT, count INTEGER, neto TEXT, iva TEXT, imp TEXT, web TEXT, cotizacion TEXT, name TEXT )"
        db.execSQL(articleCreate)
        val codeCreate = "CREATE TABLE codes (id INTEGER PRIMARY KEY AUTOINCREMENT, partaker_id INTEGER,  code TEXT, status, date TEXT, id_purchase_order TEXT, id_purchase_ticket TEXT )"
        db.execSQL(codeCreate)
        val locationCreate = "CREATE TABLE locations (id INTEGER PRIMARY KEY AUTOINCREMENT, partaker_id INTEGER, name TEXT, code TEXT, status)"
        db.execSQL(locationCreate)
        val participanteCreate = " CREATE TABLE participantes (id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT, dni TEXT, name TEXT, last_name TEXT, email TEXT, phone TEXT, photo_url TEXT , date TEXT, id_user TEXT, id_event INTEGER DEFAULT 0, sex TEXT, city TEXT, status INTEGER, company TEXT, category TEXT )"
        db.execSQL(participanteCreate)
        val functionCreate = "CREATE TABLE functions(id INTEGER PRIMARY KEY AUTOINCREMENT, id_event INTEGER DEFAULT 0,   id_function INTEGER DEFAULT 0, status TEXT, category TEXT )"
        db.execSQL(functionCreate)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("MODELHELPER oldVersion", oldVersion.toString())
        Log.d("MODELHELPER newVersion", newVersion.toString())
        if (newVersion <=  1) {
            Log.d("MODELHELPER version 1", "")
            upgradeVersion1(db)

        }
        if (newVersion <=  2) {
            Log.d("MODELHELPER version 2", "")
            upgradeVersion1(db)
            upgradeVersion2(db)
        }
        if (newVersion <=  3) {
            Log.d("MODELHELPER version 3 ini", "")
            //upgradeVersion1(db)
            //upgradeVersion2(db)
            upgradeVersion3(db)
            Log.d("MODELHELPER version 3 fin", "")
        }
        if (newVersion <=  4) {
            Log.d("MODELHELPER version 4 ini", "")
            //upgradeVersion1(db)
            //upgradeVersion2(db)
            upgradeVersion4(db)
            Log.d("MODELHELPER version 4 fin", "")
        }
        if (newVersion <=  5) {
            Log.d("MODELHELPER version 4 ini", "")
            //upgradeVersion1(db)
            //upgradeVersion2(db)
            upgradeVersion5(db)
            Log.d("MODELHELPER version 4 fin", "")
        }

    }
    private fun upgradeVersion5(db: SQLiteDatabase?) {
        val functionCreate = "CREATE TABLE functions (id INTEGER PRIMARY KEY AUTOINCREMENT, id_event INTEGER DEFAULT 0,   id_function INTEGER DEFAULT 0, status TEXT, category TEXT )"
        db!!.execSQL(functionCreate)
    }
    private fun upgradeVersion3(db: SQLiteDatabase?) {
        val participanteCreate = " CREATE TABLE participantes (id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT, dni TEXT, name TEXT, last_name TEXT, email TEXT, phone TEXT, photo_url TEXT , date TEXT, id_user TEXT, id_event INTEGER DEFAULT 0, sex TEXT, city TEXT, status INTEGER, company TEXT, category TEXT )"
        db!!.execSQL(participanteCreate)
    }
    private fun upgradeVersion2(db: SQLiteDatabase?) {
        val ticketDelete = "ALTER TABLE tickets ADD id_event INTEGER DEFAULT 0 NOT NULL"
        db!!.execSQL(ticketDelete)
    }
    private fun upgradeVersion4(db: SQLiteDatabase?) {
        val ticketalterCompany = "ALTER TABLE participantes ADD company TEXT"
        db!!.execSQL(ticketalterCompany)
        val ticketalterCategory = "ALTER TABLE participantes ADD category TEXT"
        db!!.execSQL(ticketalterCategory)
    }
    private fun upgradeVersion1(db: SQLiteDatabase?) {
        val ticketDelete = "DROP TABLE IF EXISTS tickets"
        db!!.execSQL(ticketDelete)
        val partakerDelete = "DROP TABLE IF EXISTS partakers"
        db.execSQL(partakerDelete)
        val articleDelete = "DROP TABLE IF EXISTS articles"
        db.execSQL(articleDelete)
        val codeDelete = "DROP TABLE IF EXISTS codes"
        db.execSQL(codeDelete)
        val locationDelete = "DROP TABLE IF EXISTS locations"
        db.execSQL(locationDelete)
        val participanteDelete = "DROP TABLE IF EXISTS participantes"
        db.execSQL(participanteDelete)
        val functionDelete = "DROP TABLE IF EXISTS functions"
        db.execSQL(functionDelete)
        onCreate(db)
    }

    fun addTicket(code:String, status:String, token:String, date:String){
        val ticket = ContentValues()
        ticket.put("code", code)
        ticket.put("status", status)
        ticket.put("token", token)
        ticket.put("date", date)
        ticket.put("id_event", preference.getIdEvent())

        val db = this.writableDatabase
        val id=db.insertOrThrow("tickets", null, ticket)
        Log.d("MODELHELPER", "ticket = $id")
        Log.d("MODELHELPER", "ticket = ${ticket.toString()}")
        db.close()
        Log.d("MODELHELPER", "cierre")
    }
    @SuppressLint("SimpleDateFormat")
    fun addOrder(id:String, partaker:Partaker, articles:ArrayList<Articles>, codes:ArrayList<PurchaseOrderTicket>, locations:ArrayList<Location>){
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate= sdf.format(Date())
        val value = ContentValues()
        value.put("code",      id)
        value.put("dni",       partaker.id)
        value.put("name",      partaker.name)
        value.put("last_name", partaker.lastname)
        value.put("phone",     partaker.phone)
        value.put("email",     partaker.email)
        value.put("id_user",   preference.getUserMail())
        value.put("date",      currentDate.toString() )

        val db = this.writableDatabase
        db.insert("partakers", null, value)

        articles.forEach {
            value.put("partaker_id", id)
            value.put("quantity",    it.quantity)
            value.put("amount",      it.amount)
            value.put("tax",         it.tax)
            value.put("total",       it.total)
            value.put("cantidad",    it.cantidad)
            value.put("neto",        it.neto)
            value.put("imp",         it.imp)
            value.put("web",         it.web)
            value.put("cotizacion",  it.cotizacion)
            value.put("name",        it.name)
            db.insert("articles", null, value)
        }
        codes.forEach {
            value.put("partaker_id",        id)
            value.put("code",               it.code)
            value.put("status",             it.status)
            value.put("id_purchase_order",  it.id_purchase_order)
            value.put("id_purchase_ticket", it.id_purchase_ticket)
            db.insert("codes", null, value)
        }
        locations.forEach {
            value.put("partaker_id",        id)
            value.put("name",               it.name)
            value.put("status",             "")
            db.insert("locations", null, value)
        }
        db.close()
    }
    @SuppressLint("Recycle")
    fun ListTicket(): MutableList<Ticket> {

        val db = this.readableDatabase
        val id_event = preference.getIdEvent()
        Log.d("MODELHELPER", "id_event = $id_event")
        val query:String
        if(id_event>1){
            query = "SELECT * FROM tickets WHERE id_event=$id_event ORDER BY id DESC"
            Log.d("MODELHELPER", "query = $query")
        }else{
            query = "SELECT * FROM tickets  ORDER BY id DESC"
            Log.d("MODELHELPER", "query = $query")
        }
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            do{
                /*Log.d("MODELHELPER", "0- tikest = ${cursor.getInt(0).toString()}")
                Log.d("MODELHELPER", "1- tikest = ${cursor.getStringOrNull(1).toString()}")
                Log.d("MODELHELPER", "3- tikest = ${cursor.getStringOrNull(3).toString()}")
                Log.d("MODELHELPER", "4- tikest = ${cursor.getStringOrNull(4).toString()}")
                Log.d("MODELHELPER", "5- tikest = ${cursor.getInt(5).toString()}")
                Log.d("MODELHELPER", "2- tikest = ${cursor.getStringOrNull(2).toString()}")
                Log.d("MODELHELPER", "-----------------------------")*/
                dogCode.add(Ticket(cursor.getString(1).toString(), cursor.getString(4).toString()) )
            }while(cursor.moveToNext())
        }
        Log.d("MODELHELPER", "count tikest = ${dogCode.count()}")
        return dogCode
    }
    fun addPartaker(code:String, dni:String, name:String, last_name:String, email:String, phone:String, photo_url:String , id_user:String, sex:String, status:Int, city:String, company:String, category:String){
        val it = ContentValues()
        it.put("code", code)
        it.put("dni", dni)
        it.put("name", name)
        it.put("last_name", last_name)
        it.put("email", email)
        it.put("phone", phone)
        it.put("photo_url", photo_url)
        it.put("id_user", id_user)
        it.put("id_event", preference.getIdEvent())
        it.put("sex", sex)
        it.put("status", status)
        it.put("city", city)
        it.put("category", category)
        it.put("company", company)


        val db = this.writableDatabase
        val id=db.insertOrThrow("participantes", null, it)
        Log.d("MODELHELPER", "ticket = $id")
        Log.d("MODELHELPER", "ticket = ${it.toString()}")
        db.close()
        Log.d("MODELHELPER", "cierre")
    }
    @SuppressLint("Recycle")
    fun ListPartaker():MutableList<Partaker> {

        val db = this.readableDatabase
        val id_event = preference.getIdEvent()
        val query:String
        if(id_event>1){
            query = "SELECT * FROM participantes WHERE id_event=$id_event ORDER BY id DESC"
            Log.d("MODELHELPER 1", "query = $query")
        }else{
            query = "SELECT * FROM participantes  ORDER BY id DESC"
            Log.d("MODELHELPER 2", "query = $query")
        }

        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            do{
                val partaker = Partaker(
                    cursor.getInt(0),
                    cursor.getString(2).toString(),
                    cursor.getString(3).toString(),
                    cursor.getString(4).toString(),
                    cursor.getString(8),
                    cursor.getString(11).toString(),
                    cursor.getString(5).toString(),
                    cursor.getString(15).toString(),
                    cursor.getString(6).toString(),
                    cursor.getInt(9),
                    cursor.getString(12).toString(),
                    cursor.getInt(13),
                    cursor.getString(1).toString(),
                    cursor.getString(14).toString()
                )
                dogPartaker.add(partaker)
            }while(cursor.moveToNext())
        }
        Log.d("MODELHELPER 3", "count partaker = ${dogPartaker.count()}")
        return dogPartaker
    }
    fun addFunction( id_function:Int, status:String, category:String){
        val function = ContentValues()
        function.put("id_function", id_function)
        function.put("status", status)
        function.put("category", category)
        function.put("id_event", preference.getIdEvent())

        val db = this.writableDatabase
        val id=db.insertOrThrow("functions", null, function)
        Log.d("MODELHELPER ADD", "functions = $id")
        db.close()
    }
    @SuppressLint("Recycle")
    fun ListFunction():MutableList<Function> {

        val db = this.readableDatabase
        val query:String
        if(preference.getIdEvent()>1){
            query = "SELECT * FROM functions WHERE id_event=${preference.getIdEvent()} ORDER BY id DESC"
            Log.d("MODELHELPER 1", "query = $query")
        }else{
            query = "SELECT * FROM functions id_event= 0 ORDER BY id DESC"
            Log.d("MODELHELPER 2", "query = $query")
        }

        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            do{
                val function = Function(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                )
                dogFunction.add(function)
            }while(cursor.moveToNext())
        }
        Log.d("MODELHELPER 3", "count function = ${dogPartaker.count()}")
        return dogFunction
    }
    @SuppressLint("Recycle")
    fun ShowFunction(id_function:Int): Function? {

        val db = this.readableDatabase
        val query:String
        if(preference.getIdEvent()>1){
            query = "SELECT * FROM functions WHERE id_event=${preference.getIdEvent()} AND id_function=$id_function ORDER BY id DESC"
            Log.d("MODELHELPER 1", "query = $query")
        }else{
            query = "SELECT * FROM functions id_event= 0 ORDER BY id DESC"
            Log.d("MODELHELPER 2", "query = $query")
        }

        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){

                val function = Function(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                )
            Log.d("MODELHELPER Return function", function.id_function.toString())
                return function

        }else{
            Log.d("MODELHELPER Return null", "0")
            return  null
        }


    }
    fun functionDelete(id_function: Int){
        val db = this.writableDatabase
        db.delete("functions", "id_event=? and id_function=?", arrayOf<String>(preference.getIdEvent().toString(), id_function.toString()))
    }

}