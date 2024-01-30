package com.umitytsr.peti.view.home.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umitytsr.peti.data.model.ChatCardModel
import com.umitytsr.peti.databinding.ItemRowChatBinding

class ChatAdapter(
    private val myChatList: List<ChatCardModel>,
    private val chatItemClickListener: ChatItemClickListener
) :
    RecyclerView.Adapter<ChatAdapter.MyChatViewHolder>() {

    inner class MyChatViewHolder(private val binding: ItemRowChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(chatCardModel: ChatCardModel){
                with(binding){
                    receiverNameTextView.text = "${chatCardModel.receiver} - ${chatCardModel.petName}"
                    if (chatCardModel.receiverImage != ""){
                        Glide.with(itemView.context)
                            .load(chatCardModel.receiverImage)
                            .into(receiverImage)
                    }

                    chatCardView.setOnClickListener {
                        chatItemClickListener.chatCardClickedListener(chatCardModel)
                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRowChatBinding.inflate(layoutInflater,parent,false)

        return MyChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return myChatList.size
    }

    override fun onBindViewHolder(holder: MyChatViewHolder, position: Int) {
        holder.bind(myChatList[position])
    }

    interface ChatItemClickListener{
        fun chatCardClickedListener(chatCardModel: ChatCardModel)
    }
}