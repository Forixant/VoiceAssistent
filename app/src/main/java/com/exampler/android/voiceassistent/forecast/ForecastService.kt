package com.exampler.android.voiceassistent.forecast

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ForecastService {
    val api: ForecastApi
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://api.weatherstack.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build()
            return retrofit.create(ForecastApi::class.java) //Создание объекта, при помощи которого будут выполняться запросы
        }
}