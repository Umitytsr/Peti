package com.umitytsr.peti.view.home.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.umitytsr.peti.R
import com.umitytsr.peti.data.model.Message
import com.umitytsr.peti.databinding.ItemRowDateWithReceiveBinding
import com.umitytsr.peti.databinding.ItemRowDateWithSendBinding
import com.umitytsr.peti.databinding.ItemRowReceiveMessageBinding
import com.umitytsr.peti.databinding.ItemRowSendMessageBinding
import com.umitytsr.peti.util.formatTimestampToDayMonthYear
import com.umitytsr.peti.util.formatTimestampToTime
import com.umitytsr.peti.util.isDateChanged

class MessageAdapter(private val userEmail: String, private val messageList: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_RECEIVE = 1
        const val ITEM_SEND = 2
        const val ITEM_DATE_SENDER = 3
        const val ITEM_DATE_RECEIVER = 4
    }

    class SendViewHolder(private val binding: ItemRowSendMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            with(binding){
                sendMessage.text = message.message
                timeSendMessageTextView.text = formatTimestampToTime(message.date!!)
            }
        }
    }

    class ReceiveViewHolder(private val binding: ItemRowReceiveMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            with(binding){
                receiveMessage.text = message.message
                timeReceiveMessageTextView.text = formatTimestampToTime(message.date!!)
            }
        }
    }

    class DateSenderViewHolder(private val binding: ItemRowDateWithSendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            with(binding){
                dateMessageWithSenderTextView.text = formatTimestampToDayMonthYear(itemView.context, message.date!!)
                sendWithDateMessage.text = message.message
                timeSendMessageWithDateTextView.text = formatTimestampToTime(message.date!!)
            }
        }
    }

    class DateReceiverViewHolder(private val binding: ItemRowDateWithReceiveBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            with(binding){
                dateMessageWithReceiverTextView.text = formatTimestampToDayMonthYear(itemView.context, message.date!!)
                receiveWithDateMessage.text = message.message
                timeReceiveMessageWithDateTextView.text = formatTimestampToTime(message.date!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_RECEIVE -> ReceiveViewHolder(ItemRowReceiveMessageBinding.inflate(inflater, parent, false))
            ITEM_SEND -> SendViewHolder(ItemRowSendMessageBinding.inflate(inflater, parent, false))
            ITEM_DATE_SENDER -> DateSenderViewHolder(ItemRowDateWithSendBinding.inflate(inflater, parent, false))
            ITEM_DATE_RECEIVER -> DateReceiverViewHolder(ItemRowDateWithReceiveBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        when (holder) {
            is SendViewHolder -> holder.bind(currentMessage)
            is ReceiveViewHolder -> holder.bind(currentMessage)
            is DateSenderViewHolder -> holder.bind(currentMessage)
            is DateReceiverViewHolder -> holder.bind(currentMessage)
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if ((position == 0 || isDateChanged(messageList[position - 1], currentMessage)) && userEmail == currentMessage.senderEmail ) {
            return ITEM_DATE_SENDER
        } else if((position == 0 || isDateChanged(messageList[position - 1], currentMessage)) && userEmail != currentMessage.senderEmail){
            return ITEM_DATE_RECEIVER
        }
        else if (userEmail == currentMessage.senderEmail) {
            return ITEM_SEND
        } else {
            return ITEM_RECEIVE
        }
    }
}