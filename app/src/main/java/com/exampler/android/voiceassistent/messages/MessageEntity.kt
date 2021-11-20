package com.exampler.android.voiceassistent.messages

class MessageEntity {
    var text: String
    var date = ""
    var isSend: Int

    constructor(text: String, date: String, isSend: Int) {
        this.date = date
        this.text = text
        this.isSend = isSend
    }

    constructor(message: Message) {
        text = message.text
        isSend = if (message.isSend!!) 1 else 0
        date = message.date.toString()
    }
}