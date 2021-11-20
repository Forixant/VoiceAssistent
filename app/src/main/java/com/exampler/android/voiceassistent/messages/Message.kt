package com.exampler.android.voiceassistent.messages

import java.io.Serializable
import java.util.*


class Message : Serializable {
    var text: String
    var date: Date
    var isSend: Boolean? = null

    constructor(text: String, isSend: Boolean?) {
        this.text = text
        this.isSend = isSend
        date = Date()
    }

    constructor(enitity: MessageEntity) {
        text = enitity.text
        date = Date(enitity.date)
        if (enitity.isSend === 1) isSend = true else isSend = false
    }
}
