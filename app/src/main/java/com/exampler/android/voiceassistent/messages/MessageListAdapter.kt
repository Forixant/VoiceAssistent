package com.exampler.android.voiceassistent.messages


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.exampler.android.voiceassistent.R
import java.util.ArrayList


class MessageListAdapter : Adapter<RecyclerView.ViewHolder>() {
    var messageList: MutableList<Message> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = if (viewType == USER_TYPE) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_message, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.assistant_message, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messageViewHolder = MessageViewHolder(holder.itemView)
        messageViewHolder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(index: Int): Int {
        val message = messageList[index]
        return if (message.isSend!!) {
            USER_TYPE
        } else ASSISTANT_TYPE
    }

    companion object {
        private const val ASSISTANT_TYPE = 0
        private const val USER_TYPE = 1
    }
}
