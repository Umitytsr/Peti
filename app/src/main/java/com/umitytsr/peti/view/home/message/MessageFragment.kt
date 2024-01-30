package com.umitytsr.peti.view.home.message

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.umitytsr.peti.data.model.Message
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.FragmentMessageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MessageFragment : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private val viewModel: MessageViewModel by viewModels()
    private val args: MessageFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        getData()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        if (args.previousFragment == "homeFragment") {
            viewModel.createChat(args.petModel.petName!!, args.petModel.petOwnerEmail!!)
            viewModel.fetchMyAllMessageWithInfoFragment(
                args.petModel.petName!!,
                args.petModel.petOwnerEmail!!
            )
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.messageResult.collectLatest {
                    initRecyclerView(auth.currentUser!!.email, it)
                }
            }
            viewModel.getReceiverInfo(args.petModel.petOwnerEmail!!)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.userResult.collectLatest {
                    with(binding){
                        if (it.userImage != ""){
                            Glide.with(requireContext()).load(it.userImage).into(receiverImage)
                        }
                        receiverNameTextView.text = "${it.userFullName}-${args.petModel.petName}"
                    }
                }
            }
            binding.infoPetButton.visibility = View.INVISIBLE
        } else {
            viewModel.fetchMyAllMessageWithChatFragment(
                args.chatCardModel.receiverEmail!!,
                args.chatCardModel.chatDocPath!!
            )
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.messageResult.collectLatest {
                    initRecyclerView(auth.currentUser!!.email,it)
                }
            }
            viewModel.getReceiverInfo(args.chatCardModel.receiverEmail!!)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.userResult.collectLatest {
                    with(binding){
                        if (it.userImage != ""){
                            Glide.with(requireContext()).load(it.userImage).into(receiverImage)
                        }
                        receiverNameTextView.text = "${it.userFullName}-${args.chatCardModel.petName}"
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            arrowBackButton.setOnClickListener {
                if (args.previousFragment == "homeFragment") {
                    findNavController().navigate(
                        MessageFragmentDirections.actionMessageFragmentToInfoFragment(
                            args.petModel,
                            "homeFragment"
                        )
                    )
                } else {
                    findNavController().navigate(
                        MessageFragmentDirections.actionMessageFragmentToChatFragment()
                    )
                }
            }
            var petInfo = PetModel()
            if (args.previousFragment == "chatFragment"){
                viewLifecycleOwner.lifecycleScope.launch {
                    launch {
                        viewModel.getPetData(args.chatCardModel.petOwnerEmail!!,args.chatCardModel.petName!!)
                    }
                    launch {
                        viewModel.petResult.collectLatest {
                            petInfo = it
                        }
                    }
                }
            }
            infoPetButton.setOnClickListener {
                findNavController().navigate(
                    MessageFragmentDirections.actionMessageFragmentToInfoFragment(petInfo,args.previousFragment)
                )
            }

            sendMessageButton.setOnClickListener {
                val message = messageEditText.text.toString()
                if (args.previousFragment == "homeFragment") {
                    viewModel.sendMessageFromInfoFragment(
                        args.petModel.petName!!,
                        args.petModel.petOwnerEmail!!,
                        message
                    )
                } else {
                    viewModel.sendMessageFromChatFragment(
                        args.chatCardModel.receiverEmail!!,
                        args.chatCardModel.chatDocPath!!,
                        message
                    )
                }
                messageEditText.setText("")
            }
        }
    }

    private fun initRecyclerView(userEmail: String?, messageList: List<Message>) {
        val _adapter = MessageAdapter(userEmail!!, messageList)
        val _layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false).apply {
                stackFromEnd = true
            }
        binding.messageRecyclerView.apply {
            adapter = _adapter
            layoutManager = _layoutManager
        }
    }
}