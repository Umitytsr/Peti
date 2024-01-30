package com.umitytsr.peti.view.home.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umitytsr.peti.data.model.ChatCardModel
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment(), ChatAdapter.ChatItemClickListener {
    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        getMyChatList()
        return binding.root
    }

    private fun getMyChatList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chatListResult.collectLatest {
                if (it.isNotEmpty()) {
                    with(binding) {
                        emtyListImageView.visibility = View.INVISIBLE
                        emptyListTextView.visibility = View.INVISIBLE
                        youHaventTextView.visibility = View.INVISIBLE
                        chatFragmentRecyclerView.visibility = View.VISIBLE
                    }
                    initRecyclerView(it)
                } else {
                    with(binding) {
                        emtyListImageView.visibility = View.VISIBLE
                        emptyListTextView.visibility = View.VISIBLE
                        youHaventTextView.visibility = View.VISIBLE
                        chatFragmentRecyclerView.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun initRecyclerView(chatCardModel: List<ChatCardModel>) {
        val _adapter = ChatAdapter(chatCardModel, this@ChatFragment)
        val _layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        with(binding.chatFragmentRecyclerView) {
            adapter = _adapter
            layoutManager = _layoutManager
        }
    }

    override fun chatCardClickedListener(chatCardModel: ChatCardModel) {
        val petModel = PetModel()
        findNavController().navigate(
            ChatFragmentDirections.actionChatFragmentToMessageFragment(petModel,"chatFragment",chatCardModel)
        )
    }
}