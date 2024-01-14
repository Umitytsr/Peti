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
                viewModel.getUserData(args.petModel.petOwner)
            }
            launch {
                viewModel.userResult.collectLatest {
                    if (it.userPhoneNumber.isNotEmpty()){
                        binding.callButton.visibility = View.VISIBLE
                        userPhoneNumber = it.userPhoneNumber
                    }else{
                        binding.callButton.visibility = View.GONE
                    }
                }
            }
            launch {
                viewModel.petOwnerEquals(args.petModel.petOwner)
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
        }

        with(binding){
            Glide.with(requireContext()).load(args.petModel.petImage).into(petImage)
            petNameTextView.text = args.petModel.petName
            petAgeTextView.text = args.petModel.petAge
            petBreedTextView.text = args.petModel.petBreed
            petSexTextView.text = args.petModel.petSex
            petVacTextView.text = args.petModel.petVaccination
            petGoalTextView.text = args.petModel.petGoal
            petDescriptionTextView.text = args.petModel.petDescription
            dateTextView.text = formatTimestampToDayMonthYear(args.petModel.date!!)
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