package com.exampler.android.voiceassistent.forecast

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer
import kotlin.math.abs


object ForecastToString {
    fun getForecast(city: String?, callback: Consumer<String?>) {
        val api: ForecastApi = ForecastService.api
        val call = api.getCurrentWeather(city)
        call!!.enqueue(object : Callback<Forecast?> {
            override fun onResponse(call: Call<Forecast?>, response: Response<Forecast?>) {
                val result = response.body()
                if (result != null) {
                    val answer =
                        "сейчас где-то " + result.current!!.temperature.toString() + ending(
                            result.current!!.temperature
                        ) + " и " + result.current!!.weather_descriptions!![0]
                    callback.accept(answer)
                } else callback.accept("Не могу узнать погоду")
            }

            override fun onFailure(call: Call<Forecast?>, t: Throwable) {
                Log.w("WEATHER", t.message!!)
            }
        })
    }

    fun ending(a: Int?): String {
        var str = ""
        val tmp = abs(a!!)
        str =
            if (tmp % 10 == 1 && tmp % 100 != 11) " градус" else if (tmp % 10 in 2..4 && (tmp % 100 < 10 || tmp % 100 >= 20)) " градуса" else " градусов"
        return str
    }
}

