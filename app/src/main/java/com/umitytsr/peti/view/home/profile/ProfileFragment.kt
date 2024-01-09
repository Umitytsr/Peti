package com.umitytsr.peti.view.home.profile

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
import com.bumptech.glide.Glide
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        getData()
        return binding.root
    }

    private fun getData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.petListResult.collectLatest {
                initRecyclerView(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.userResult.collectLatest {user ->
                with(binding){
                    userNameTextView.text = user.userFullName
                    if (user.userImage != ""){
                        Glide.with(requireContext()).load(user.userImage).into(profileImage)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            settingsButton.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
                )
            }
        }
    }

    private fun initRecyclerView(petList: List<PetModel>) {
        val _adapter = ProfileAdapter(petList)
        val _layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.profileRecyclerView.apply {
            adapter = _adapter
            layoutManager = _layoutManager
        }
    }
}