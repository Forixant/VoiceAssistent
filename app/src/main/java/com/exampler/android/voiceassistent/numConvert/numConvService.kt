package com.exampler.android.voiceassistent.numConvert

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object numConvService {
    val api: numConvApi
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://htmlweb.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(numConvApi::class.java)
        }
}
