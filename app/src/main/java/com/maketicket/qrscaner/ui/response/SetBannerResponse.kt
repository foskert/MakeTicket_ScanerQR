package com.maketicket.qrscaner.ui.response

import com.google.gson.annotations.SerializedName
import com.maketicket.qrscaner.ui.model.Banner

class SetBannerResponse(
    @SerializedName("data")  val data: Banner
)