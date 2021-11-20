package com.exampler.android.voiceassistent.parse


import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.lang.StringBuilder


object ParcingHtmlService {
    @Throws(IOException::class)
    fun getHoliday(date: String): String {
        var URL = "http://mirkosmosa.ru/holiday/"
        URL += date.substring(date.length - 4)
        val document: Document = Jsoup.connect(URL).get()
        val body: Element = document.body()
        val elem: Elements = body.select("div.month_cel_date>span") //даты
        val hols: Elements = body.select("div.month_cel") //праздники
        var ind = -1 //индекс текущей даты
        //получаем индекс, если такая дата есть
        for (el in elem) {
            if (el.text().matches(Regex(date))) {
                ind = elem.indexOf(el)
                break
            }
        }
        if (ind == -1) return "Такая дата отсутствует в календаре"

        //получаем все праздники текущей даты, сохраняем в hol через ,
        val hol = StringBuilder()
        val prtmp: Elements =
            hols.get(ind).select("div.month_cel > ul > li") //список праздников по индексу
        for (i in 0 until prtmp.size) {
            hol.append(prtmp.get(i).text())
            if (i + 1 < prtmp.size) hol.append(", ")
        }
        return if (hol.toString().isEmpty()) {
            "Праздника в эту дату нет"
        } else hol.toString()
    }
}
