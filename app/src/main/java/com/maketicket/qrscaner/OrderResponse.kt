package com.maketicket.qrscaner

import com.google.gson.annotations.SerializedName
import com.google.rpc.Code

class OrderResponse {
    @SerializedName("code")
    var code: Code?= null
}