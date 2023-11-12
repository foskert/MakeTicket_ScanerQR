package com.maketicket.qrscaner

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestEngine {
    companion object{
        fun getRestEngine(): Retrofit {
            //val interceptor = HttpLoggingInterceptor()
            //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            //val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            //.baseUrl("https://www.makeidsystems.com/makeid/")
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.makeidsystems.com/makeid/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                //.client(client)


            return retrofit
        }
    }
}