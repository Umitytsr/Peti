package com.umitytsr.peti.view.home.editProfile

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.umitytsr.peti.databinding.FragmentEditProfileBinding
import com.umitytsr.peti.util.GalleryUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private lateinit var binding : FragmentEditProfileBinding
    private lateinit var galleryUtility: GalleryUtility
    var selectedPicture: Uri? = null
    private val viewModel : EditProfileViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater,container,false)
        galleryUtility = GalleryUtility(this)
        getUserData()
        return binding.root
    }

    private fun getUserData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userResult.collectLatest {
                with(binding){
                    userFullNameEditText.editText?.setText(it.userFullName)
                    if (it.userImage != ""){
                        Glide.with(requireContext()).load(it.userImage).into(userProfileImage)
                    }
                    if (it.userPhoneNumber != ""){
                        userPhoneNumberEditText.editText?.setText(it.userPhoneNumber)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            arrowBackButton.setOnClickListener {
                findNavController().popBackStack()
            }

            editUserImageButton.setOnClickListener {
                galleryUtility.selectImageFromGallery { uri ->
                    uri?.let {
                        userProfileImage.setImageURI(it)
                        selectedPicture = it
                    }
                }
            }

            saveButton.setOnClickListener {
                val userFirstName = userFullNameEditText.editText?.text.toString()
                val userPhoneNumber = userPhoneNumberEditText.editText?.text.toString()
                viewModel.updateUserInfo(userFirstName, userPhoneNumber,selectedPicture)
                lifecycleScope.launch {
                    viewModel.navigateResult.collect{navigate ->
                        if (navigate){
                            findNavController().navigate(
                                EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment()
                            )
                            viewModel.onNavigateDone()
                        }
                    }
                }
            }
        }
    }
}