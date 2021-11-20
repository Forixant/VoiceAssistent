package com.exampler.android.voiceassistent

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exampler.android.voiceassistent.database.DBHelper
import com.exampler.android.voiceassistent.messages.Message
import com.exampler.android.voiceassistent.messages.MessageEntity
import com.exampler.android.voiceassistent.messages.MessageListAdapter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var sendButton: Button
    private lateinit var questionText: EditText
    lateinit var textToSpeech: TextToSpeech
    lateinit var chatMessageList: RecyclerView
    lateinit var messageListAdapter: MessageListAdapter
    lateinit var sPref: SharedPreferences
    private var isLight = true
    private val THEME = "THEME"
    lateinit var dBHelper: DBHelper
    var database: SQLiteDatabase? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        //тема
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        isLight = sPref.getBoolean(THEME, true)
        if (!isLight) {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dBHelper = DBHelper(this)
        database = dBHelper.writableDatabase

        sendButton = findViewById(R.id.sendButton)
        questionText = findViewById(R.id.questionField)
        chatMessageList = findViewById(R.id.chatMessageList)

        messageListAdapter = MessageListAdapter()
        chatMessageList.layoutManager = LinearLayoutManager(this)
        chatMessageList.adapter = messageListAdapter

        //востановление сообщений
        if (savedInstanceState != null) {
            val smList: List<Message>? =
                savedInstanceState.getSerializable("messages") as java.util.ArrayList<Message>?
            for (i in smList!!.indices) messageListAdapter.messageList.add(
                smList[i]
            )
            messageListAdapter.notifyDataSetChanged()
            chatMessageList.scrollToPosition(smList.size - 1)
        } else {
            val cursor = database!!.query(
                DBHelper.TABLE_MESSAGES, null, null, null,
                null, null, null
            )
            if (cursor.moveToFirst()) {
                val messageIndex = cursor.getColumnIndex(DBHelper.FIELD_MESSAGE)
                val dateIndex = cursor.getColumnIndex(DBHelper.FIELD_DATE)
                val sendIndex = cursor.getColumnIndex(DBHelper.FIELD_SEND)
                do {
                    val entity = MessageEntity(
                        cursor.getString(messageIndex),
                        cursor.getString(dateIndex), cursor.getInt(sendIndex)
                    )
                    val message = Message(entity)
                    messageListAdapter.messageList.add(message)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }

        sendButton.setOnClickListener(View.OnClickListener { onSend() })

        textToSpeech = TextToSpeech(
            applicationContext
        ) { i ->
            if (i != TextToSpeech.ERROR) {
                textToSpeech.language = Locale("ru")
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onSend() {
        val question = questionText.text.toString()
        messageListAdapter.messageList.add(Message(question, true))
        AI.getAnswer(question) { s ->
            messageListAdapter.messageList.add(Message(s, false))
            messageListAdapter.notifyDataSetChanged()
            chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)
            textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null)
        }
        questionText.setText("")
    }

    override fun onStop() {
        database!!.delete(DBHelper.TABLE_MESSAGES, null, null)
        for (i in 0 until messageListAdapter.messageList.size) {
            val enitity = MessageEntity(messageListAdapter.messageList[i])
            val contentValues = ContentValues()
            contentValues.put(DBHelper.FIELD_MESSAGE, enitity.text)
            contentValues.put(DBHelper.FIELD_SEND, enitity.isSend)
            contentValues.put(DBHelper.FIELD_DATE, enitity.date)
            database!!.insert(DBHelper.TABLE_MESSAGES, null, contentValues)
        }
        val editor = sPref.edit()
        editor.putBoolean(THEME, isLight)
        editor.apply()
        super.onStop()
    }

    //сохранение состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("messages", messageListAdapter.messageList as ArrayList<Message>)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.day_settings -> {
                //установка дневной темы
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                isLight = true
            }
            R.id.night_settings -> {
                //установка ночной темы
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                isLight = false
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val APP_PREFERENCES = "mysettings"
    }
}