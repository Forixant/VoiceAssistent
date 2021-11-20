package com.exampler.android.voiceassistent

import android.annotation.SuppressLint
import com.exampler.android.voiceassistent.forecast.ForecastToString
import com.exampler.android.voiceassistent.numConvert.numConvToString
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import com.exampler.android.voiceassistent.parse.ParcingHtmlService.getHoliday
import java.lang.StringBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern


object AI {
    private val dateNow = Date()

    //преобразование слов в даты
    @SuppressLint("DefaultLocale")
    private fun getDate(str: String): String {
        val instance = Calendar.getInstance()
        instance.time = dateNow //устанавливаем дату, с которой будет производить операции
        var newstr = str.substring(9)
        newstr = newstr.replace("сегодня", String.format("%te %<tB %<tY", dateNow))
        instance.add(Calendar.DAY_OF_MONTH, 1) // прибавляем 1 день к установленной дате
        var newDate = instance.time // получаем измененную дату
        newstr = newstr.replace("завтра", String.format("%te %<tB %<tY", newDate))
        instance.add(Calendar.DAY_OF_MONTH, -2) // вычитаем 2 дня
        newDate = instance.time // получаем измененную дату
        newstr = newstr.replace("вчера", String.format("%te %<tB %<tY", newDate))
        return newstr
    }

    @SuppressLint("DefaultLocale")
    private fun check_phrase(quest: String, callback: Consumer<String>) {
        var answer = "Не знаю ответа на вопрос"
        if (Pattern.matches("праздник.*", quest)) {
            val newstring = getDate(quest) //строка с датам
            val pattern = Pattern.compile("\\d{1,2} [а-я]{3,8} \\d{4}")
            val matcher = pattern.matcher(newstring)
            if (matcher.find()) { //перебор дат
                Observable.fromCallable {
                    val answers = StringBuilder()
                    val tmpnew =
                        newstring.split(", ").toTypedArray()
                    for (el in tmpnew) {
                        answers.append(getHoliday(el)).append('\n')
                    }
                    answers.toString()
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { t: String? -> callback.accept(t!!)
                    }
            } else
                callback.accept("Некорректная дата")
        } else {
            if (quest.matches(Regex("преобразовать число \\d+.*"))) {
                val NumPattern = Pattern.compile("\\d+")
                val matcher = NumPattern.matcher(quest)
                if (matcher.find()) {
                    val num = quest.substring(matcher.start(), matcher.end())
                    numConvToString.getConvertNum(num
                    ) { s ->
                        if (s != null) {
                            callback.accept(s)
                        }
                    }
                }
            } else {
                if (quest.matches(Regex("погода в городе (\\p{L}+)"))) {
                    val cityPattern =
                        Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE)
                    val matcher = cityPattern.matcher(quest)
                    if (matcher.find()) {
                        val cityName = matcher.group(1)
                        ForecastToString.getForecast(cityName
                        ) { s ->
                            if (s != null) {
                                callback.accept(s)
                            }
                        }
                    }
                } else { //часть с ответами на простые вопросы
                    val lowQuest = quest.lowercase(Locale.getDefault())
                    if (Pattern.matches(".*привет.*", lowQuest)) answer = "Привет"
                    if (Pattern.matches(".*чем занимаешься.*\\?.*", lowQuest)) answer =
                        "Отвечаю на вопросы"
                    if (Pattern.matches(".*как дела.*\\?.*", lowQuest)) answer = "Неплохо"

                    //день недели
                    if (Pattern.matches(".*какой сегодня день.*", lowQuest)) {
                        answer = String.format(
                            "%s %te %<tB %<tY",
                            "Сегодняшняя дата:",
                            dateNow
                        ) //сегодняшня дата
                    }
                    //текущее время
                    if (Pattern.matches(".*который час.*", lowQuest)) {
                        answer = "Сейчас " + SimpleDateFormat.getTimeInstance().format(
                            dateNow
                        )
                    }
                    //день недели
                    if (Pattern.matches(".*какой день недели.*", lowQuest)) {
                        val dateFormat: DateFormat = SimpleDateFormat("EEEE")
                        val s = dateFormat.format(dateNow)
                        answer = "Сегодня $s"
                    }
                    //дней до даты
                    if (Pattern.matches(".*сколько дней до.*", lowQuest)) {
                        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}")
                        val matcher = pattern.matcher(lowQuest)
                        answer = if (matcher.find()) {
                            val req = lowQuest.substring(matcher.start(), matcher.end())
                            val days = ChronoUnit.DAYS.between(
                                LocalDate.parse(
                                    LocalDate.now().toString(),
                                    dtf
                                ), LocalDate.parse(req, dtf)
                            )
                            "До заданной даты $days дней" //дней до даты
                        } else {
                            "Дата введена некорректно"
                        }
                    }
                    val joiner = StringJoiner(" ")
                    joiner.add(answer)
                    callback.accept(joiner.toString())
                }
            }
        }
    }

    fun getAnswer(question: String, callback: Consumer<String>) {
        check_phrase(question, callback)
    }
}
