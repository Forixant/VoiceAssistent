package com.exampler.android.voiceassistent.numConvert

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer


object numConvToString {
    fun getConvertNum(num: String?, callback: Consumer<String?>) {
        val api: numConvApi = numConvService.api
        val call = api.getConvertNum(num, "0")
        call!!.enqueue(object : Callback<numConv?> {
            override fun onResponse(call: Call<numConv?>, response: Response<numConv?>) {
                val result = response.body()
                if (result != null) {
                    val res = result.str
                    res!!.trim { it <= ' ' }
                    callback.accept(res)
                } else callback.accept("Ошибка конвертации числа")
            }

            override fun onFailure(call: Call<numConv?>, t: Throwable) {
                Log.w("str", t.message!!)
            }
        })
    }
}
