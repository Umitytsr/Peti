package com.umitytsr.peti.view.home.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.umitytsr.peti.databinding.FragmentProfileBinding
import com.umitytsr.peti.util.applyTheme
import com.umitytsr.peti.view.authentication.mainActivity.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val mainViewModel : MainActivityViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        getData()
        return binding.root
    }

    private fun getData(){
        viewLifecycleOwner.lifecycleScope.launch{
            launch {
                viewModel.userResult.collectLatest {user ->
                    with(binding){
                        userNameTextView.text = user.userFullName
                        if (user.userImage != ""){
                            Glide.with(requireContext()).load(user.userImage).into(profileImage)
                        }
                        userEmailTextView.text = user.userEmail
                    }
                }
            }

            launch {
                mainViewModel.isCheckedResult.collectLatest {
                    binding.darkModeSwitch.isChecked = it
                    applyTheme(it)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){

            myPetCardView.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToMyPetsFragment()
                )
            }

            darkModeSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
                mainViewModel.setDarkModeEnabled(isChecked)
            }

            editProfileCardView.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
                )
            }

            logoutButton.setOnClickListener {
                viewModel.signOut(requireActivity())
            }

            languangeCardView.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToLanguageFragment()
                )
            }

            aboutDeveloperCardView.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToDeveloperFragment()
                )
            }
        }
    }
}