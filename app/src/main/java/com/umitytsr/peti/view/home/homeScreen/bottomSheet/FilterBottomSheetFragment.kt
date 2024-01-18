package com.umitytsr.peti.view.home.homeScreen.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.umitytsr.peti.databinding.FragmentFilterBottomSheetBinding
import com.umitytsr.peti.util.Enums
import com.umitytsr.peti.view.home.homeScreen.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentFilterBottomSheetBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        getSelectedFilterData()
        return binding.root
    }

    companion object {
        const val TAG = "FilterBottomSheet"
    }

    private fun getSelectedFilterData(){
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.filteredPet.collectLatest {
                it.selectedPetType?.let {
                    val selectedChipString = Enums.getPetTypeId(it).getString(requireContext())
                    setSelectedChipByText(binding.petTypeChipGroup,selectedChipString)
                }
                it.selectedPetSex?.let {
                    val selectedChipString = Enums.getPetSexById(it).getString(requireContext())
                    setSelectedChipByText(binding.petSexChipGroup,selectedChipString)
                }
                it.selectedPetGoal?.let {
                    val selectedChipString = Enums.getPetGoalById(it).getString(requireContext())
                    setSelectedChipByText(binding.petGoalChipGroup,selectedChipString)
                }
                it.selectedPetAge?.let {
                    setSelectedChipByText(binding.petAgeChipGroup,it)
                }
                it.selectedPetVac?.let {
                    val selectedChipString = Enums.getPetVaccinationById(it).getString(requireContext())
                    setSelectedChipByText(binding.petVacChipGroup,selectedChipString)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            filterButton.setOnClickListener {
                val selectedPetTypeText = getSelectedChipText(petTypeChipGroup)
                val selectedPetSexText = getSelectedChipText(petSexChipGroup)
                val selectedPetGoalText = getSelectedChipText(petGoalChipGroup)
                val selectedPetAgeText = getSelectedChipText(petAgeChipGroup)
                val selectedPetVaccinationText = getSelectedChipText(petVacChipGroup)

                homeViewModel.filterPets(
                    selectedPetTypeText,
                    selectedPetSexText,
                    selectedPetGoalText,
                    selectedPetAgeText,
                    selectedPetVaccinationText,
                    requireContext())

                dismiss()
            }

            deleteAllFilterButton.setOnClickListener {
                petTypeChipGroup.clearCheck()
                petSexChipGroup.clearCheck()
                petGoalChipGroup.clearCheck()
                petAgeChipGroup.clearCheck()
                petVacChipGroup.clearCheck()
                homeViewModel.clearFilters()
            }
        }
    }

    private fun getSelectedChipText(chipGroup: ChipGroup): String {
        val chipId = chipGroup.checkedChipId
        return if (chipId != -1) {
            val chip = chipGroup.findViewById<Chip>(chipId)
            chip.text.toString()
        } else {
            ""
        }
    }

    private fun setSelectedChipByText(chipGroup: ChipGroup, text: String) {
        val chipToSelect = chipGroup.children.firstOrNull {
            it is Chip && it.text.toString() == text
        } as? Chip
        chipToSelect?.let {
            chipGroup.check(it.id)
        }
    }
}