package com.umitytsr.peti.view.home.petInfo

import android.content.Intent
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
import com.umitytsr.peti.databinding.FragmentInfoBinding
import com.umitytsr.peti.util.Enums
import com.umitytsr.peti.util.formatTimestampToDayMonthYear
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfoFragment : Fragment() {
    private lateinit var binding : FragmentInfoBinding
    private val viewModel : InfoViewModel by viewModels()
    private val args: InfoFragmentArgs by navArgs()
    private var userPhoneNumber : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater,container,false)
        petDetailerData()
        return binding.root
    }

    private fun petDetailerData(){
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.getUserData(args.petModel.petOwnerEmail)
            }

            launch {
                viewModel.getPetData(args.petModel.petOwnerEmail,args.petModel.petName)
            }

            launch {
                viewModel.petOwnerEquals(args.petModel.petOwnerEmail)
            }

            launch {
                viewModel.userResult.collectLatest {
                    if (it.userPhoneNumber.isNotEmpty()){
                        binding.callButton.visibility = View.VISIBLE
                        userPhoneNumber = it.userPhoneNumber
                    }else{
                        binding.callButton.visibility = View.GONE
                    }
                    binding.petOwnerNameTextView.text = it.userFullName
                }
            }

            launch {
                viewModel.petOwnerEqualsResult.collectLatest {
                    if (it){
                        binding.editPetButton.visibility = View.VISIBLE
                    }else{
                        binding.editPetButton.visibility = View.GONE
                    }
                }
            }

            launch {
                viewModel.petResult.collectLatest {
                    with(binding){
                        Glide.with(requireContext()).load(it.petImage).into(petImage)
                        petNameTextView.text = it.petName
                        petBreedTextView.text = it.petBreed
                        petSexTextView.text = Enums.getPetSexById(it.petSex).getString(requireContext())
                        petAgeTextView.text = it.petAge
                        petGoalTextView.text = Enums.getPetGoalById(it.petGoal).getString(requireContext())
                        petVacTextView.text = Enums.getPetVaccinationById(it.petVaccination).getString(requireContext())
                        petDescriptionTextView.text = it.petDescription
                        dateTextView.text = formatTimestampToDayMonthYear(it.date!!)
                    }
                }
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            callButton.setOnClickListener {
                userPhoneNumber?.let { number ->
                    makePhoneCall(number)
                }
            }
            arrowBackButton.setOnClickListener {
                when(args.previousFragment){
                    "homeFragment" -> {
                        findNavController().navigate(
                            InfoFragmentDirections.actionInfoFragmentToHomeFragment()
                        )
                    }
                    "myPetsFragment" -> {
                        findNavController().navigate(
                            InfoFragmentDirections.actionInfoFragmentToMyPetsFragment()
                        )
                    }
                }
            }
            
            editPetButton.setOnClickListener {
                findNavController().navigate(
                    InfoFragmentDirections.actionInfoFragmentToEditPetFragment(args.petModel,args.previousFragment)
                )
            }
        }
    }

    private fun makePhoneCall(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to make phone call", Toast.LENGTH_SHORT).show()
        }
    }
}