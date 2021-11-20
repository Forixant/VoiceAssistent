package com.exampler.android.voiceassistent.forecast

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApi {
    @GET("addkey")
    fun getCurrentWeather(@Query("query") city: String?): Call<Forecast?>?
}