package com.umitytsr.peti.view.home.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.umitytsr.peti.databinding.FragmentAddBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    private val viewModel : AddViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        dropdownItems()
        registerLauncher()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            selectImage.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Give Permission") {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }.show()
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                } else {
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
            }

            shareButton.setOnClickListener {
                val petDescription = descriptionEditText.editText?.text.toString()
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

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    selectedPicture = result.data?.data
                    selectedPicture?.let {
                        binding.selectImage.setImageURI(it)
                    }
                }
            }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(requireContext(), "Permission needed!", Toast.LENGTH_SHORT).show()
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