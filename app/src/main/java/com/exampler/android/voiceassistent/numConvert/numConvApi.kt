package com.exampler.android.voiceassistent.numConvert

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface numConvApi {
    @GET("/json/convert/num2str")
    fun getConvertNum(
        @Query("num") number: String?,
        @Query("dec") dec: String?
    ): Call<numConv?>?
}
