package com.umitytsr.peti.view.home.add

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.umitytsr.peti.databinding.FragmentAddBinding
import com.umitytsr.peti.util.GalleryUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private lateinit var galleryUtility: GalleryUtility
    var selectedPicture: Uri? = null
    private val viewModel : AddViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        galleryUtility = GalleryUtility(this)
        dropdownItems()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            selectImage.setOnClickListener {
                galleryUtility.selectImageFromGallery { uri ->
                    uri?.let {
                        selectImage.setImageURI(it)
                        selectedPicture = it
                    }
                }
            }

            shareButton.setOnClickListener {
                val petDescription = petDescriptionEditText.editText?.text.toString()
                val petName = petNameEditText.editText?.text.toString()
                val petType = typeDropdownMenu.editText?.text.toString()
                val petSex = sexDropdownMenu.editText?.text.toString()
                val petGoal = goalDropdownMenu.editText?.text.toString()
                val petAge = ageDropdownMenu.editText?.text.toString()
                val petVaccination = vaccinationDropdownMenu.editText?.text.toString()
                val petBreed = breedEditText.editText?.text.toString()

                viewModel.addPet(selectedPicture, petDescription, petName, petType, petSex, petGoal, petAge, petVaccination, petBreed)
                lifecycleScope.launch {
                    viewModel.navigateResult.collect{navigate ->
                        if (navigate){
                            findNavController().navigate(
                                AddFragmentDirections.actionAddFragmentToHomeFragment()
                            )
                            viewModel.onNavigateDone()
                        }
                    }
                }
            }
        }
    }

    private fun dropdownItems() {
        val petType = arrayOf("Dog", "Cat", "Other")
        val petSex = arrayOf("Male", "Famale")
        val petGoal = arrayOf("Ownership", "Matching")
        val petAge = arrayOf("0-1", "1-2", "2-3", "3-4", "4-5", "5-6", "6-7", "7-8", "8+")
        val petVaccination = arrayOf("Yes", "No")
        with(binding) {
            (typeDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(petType)
            (sexDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(petSex)
            (goalDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(petGoal)
            (ageDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(petAge)
            (vaccinationDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
                petVaccination
            )
        }
    }
}