package com.exampler.android.voiceassistent.messages

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exampler.android.voiceassistent.R
import java.text.DateFormat
import java.text.SimpleDateFormat


class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected var messageText: TextView = itemView.findViewById(R.id.messageTextView)
    protected var messageDate: TextView = itemView.findViewById(R.id.messageDataView)
    fun bind(message: Message) {
        messageText.text = message.text
        @SuppressLint("SimpleDateFormat") val fmt: DateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        messageDate.text = fmt.format(message.date)
    }
}
