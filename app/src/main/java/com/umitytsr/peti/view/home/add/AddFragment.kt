package com.umitytsr.peti.view.home.add

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.umitytsr.peti.R
import com.umitytsr.peti.databinding.FragmentAddBinding
import com.umitytsr.peti.util.Enums
import com.umitytsr.peti.util.GalleryUtility
import com.umitytsr.peti.util.setSimpleItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private lateinit var galleryUtility: GalleryUtility
    var selectedPetImage: Uri? = null
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
                        selectedPetImage = it
                    }
                }
            }

            shareButton.setOnClickListener {
                val petName = petNameEditText.editText?.text.toString()

                val petTypeString = typeDropdownMenu.editText?.text.toString()
                val petType = Enums.getPetTypeIdByString(petTypeString,requireContext())

                val petSexString = sexDropdownMenu.editText?.text.toString()
                val petSex = Enums.getPetSexIdByString(petSexString,requireContext())

                val petGoalString = goalDropdownMenu.editText?.text.toString()
                val petGoal = Enums.getPetGoalIdByString(petGoalString,requireContext())

                val petAge = ageDropdownMenu.editText?.text.toString()

                val petVaccinationString = vaccinationDropdownMenu.editText?.text.toString()
                val petVaccination = Enums.getPetVaccinationIdByString(petVaccinationString,requireContext())

                val petBreed = breedEditText.editText?.text.toString()

                val petDescription = petDescriptionEditText.editText?.text.toString()

                if (selectedPetImage != null && petName.isNotEmpty() && petTypeString.isNotEmpty() &&
                    petSexString.isNotEmpty() && petGoalString.isNotEmpty() && petAge.isNotEmpty() &&
                    petVaccinationString.isNotEmpty() && petBreed.isNotEmpty() && petDescription.isNotEmpty()
                ){
                    viewModel.addPet(selectedPetImage,petName,petType,petSex,petGoal,petAge,petVaccination,petBreed,petDescription)
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
                }else{
                    Toast.makeText(context, getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                }


            }
        }
    }

    private fun dropdownItems() {
        val petAge = arrayOf("0-1", "1-2", "2-3", "3-4", "4-5", "5-6", "6-7", "7-8", "8+")
        with(binding) {
            (ageDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(petAge)
            (typeDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItem(
                requireContext(),
                Enums.PetType.values().map { it.getString(requireContext()) }
            )
            (sexDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItem(
                requireContext(),
                Enums.PetSex.values().map { it.getString(requireContext()) }
            )
            (goalDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItem(
                requireContext(),
                Enums.PetGoal.values().map { it.getString(requireContext()) }
            )
            (vaccinationDropdownMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItem(
                requireContext(),
                Enums.PetVaccination.values().map { it.getString(requireContext()) }
            )
        }
    }
}