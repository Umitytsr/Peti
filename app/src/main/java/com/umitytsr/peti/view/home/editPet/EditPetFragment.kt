package com.umitytsr.peti.view.home.editPet

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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.umitytsr.peti.R
import com.umitytsr.peti.databinding.FragmentEditPetBinding
import com.umitytsr.peti.util.Enums
import com.umitytsr.peti.util.GalleryUtility
import com.umitytsr.peti.util.getIdForEnumString
import com.umitytsr.peti.util.getStringForEnumById
import com.umitytsr.peti.util.setSimpleItem
import com.umitytsr.peti.view.home.petInfo.InfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditPetFragment : Fragment() {
    private lateinit var binding: FragmentEditPetBinding
    private val args: EditPetFragmentArgs by navArgs()
    private lateinit var galleryUtility: GalleryUtility
    var newPetPicture: Uri? = null
    private val viewModel: EditPetViewModel by viewModels()
    private val infoFragmentViewModel: InfoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPetBinding.inflate(layoutInflater, container, false)
        dropdownItems()
        getPetData()
        galleryUtility = GalleryUtility(this)
        return binding.root
    }

    private fun getPetData() {
        with(binding) {
            Glide.with(requireContext()).load(args.petModel.petImage).into(petImage)
            petNameEditText.editText?.setText(args.petModel.petName)
            typeDropdownMenu.helperText =
                getStringForEnumById<Enums.PetType>(args.petModel.petType,requireContext())
            sexDropdownMenu.helperText =
                getStringForEnumById<Enums.PetSex>(args.petModel.petSex,requireContext())
            goalDropdownMenu.helperText =
                getStringForEnumById<Enums.PetGoal>(args.petModel.petGoal,requireContext())
            ageDropdownMenu.helperText = args.petModel.petAge
            vaccinationDropdownMenu.helperText =
                getStringForEnumById<Enums.PetVaccination>(args.petModel.petVaccination,requireContext())
            breedEditText.editText?.setText(args.petModel.petBreed)
            petDescriptionEditText.editText?.setText(args.petModel.petDescription)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            arrowBackButton.setOnClickListener {
                findNavController().navigate(
                    EditPetFragmentDirections.actionEditPetFragmentToInfoFragment(
                        args.petModel, args.previousFragment
                    )
                )
            }

            editPetImageButton.setOnClickListener {
                galleryUtility.selectImageFromGallery { uri ->
                    uri?.let {
                        petImage.setImageURI(it)
                        newPetPicture = it
                    }
                }
            }

            editButton.setOnClickListener {
                val oldPetPicture = args.petModel.petImage

                val oldPetName = args.petModel.petName
                val newPetName = petNameEditText.editText?.text.toString()

                val petTypeString = typeDropdownMenu.editText?.text.toString()
                val petType = getIdForEnumString<Enums.PetType>(petTypeString,requireContext())

                val petSexString = sexDropdownMenu.editText?.text.toString()
                val petSex = getIdForEnumString<Enums.PetSex>(petSexString,requireContext())

                val petGoalString = goalDropdownMenu.editText?.text.toString()
                val petGoal = getIdForEnumString<Enums.PetGoal>(petGoalString,requireContext())

                val petAge = ageDropdownMenu.editText?.text.toString()

                val petVaccinationString = vaccinationDropdownMenu.editText?.text.toString()
                val petVaccination = getIdForEnumString<Enums.PetVaccination>(petVaccinationString,requireContext())

                val petBreed = breedEditText.editText?.text.toString()
                val petDescription = petDescriptionEditText.editText?.text.toString()
                val date = args.petModel.date

                if (newPetName.isNotEmpty() && petTypeString.isNotEmpty() && petSexString.isNotEmpty() &&
                    petGoalString.isNotEmpty() && petAge.isNotEmpty() && petVaccinationString.isNotEmpty() &&
                    petBreed.isNotEmpty() && petDescription.isNotEmpty()
                ) {
                    viewModel.updatePet(
                        oldPetPicture, newPetPicture, oldPetName, newPetName, petType,
                        petSex, petGoal, petAge, petVaccination, petBreed, petDescription, date
                    )
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.please_fill_in_all_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                lifecycleScope.launch {
                    launch {
                        infoFragmentViewModel.getPetData(args.petModel.petOwnerEmail, newPetName)
                    }

                    launch {
                        viewModel.navigateResult.collect { navigate ->
                            if (navigate) {
                                navController()
                                viewModel.onNavigateDone()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navController() {
        lifecycleScope.launch {
            infoFragmentViewModel.petResult.collectLatest {
                findNavController().navigate(
                    EditPetFragmentDirections.actionEditPetFragmentToInfoFragment(
                        it,
                        args.previousFragment
                    )
                )
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