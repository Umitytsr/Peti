package com.umitytsr.peti.view.home.homeScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel : HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        getData()
        return binding.root
    }

    private fun getData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.petListResult.collectLatest {
                initRecyclerView(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            selectedPetChipGroup.setOnCheckedChangeListener { group, checkedId ->
                val selectedChip = group.findViewById<Chip>(checkedId)
                val selectedChipText = selectedChip?.text.toString()

                if (selectedChip != null){
                    val filteredList = viewModel.petListResult.value.filter {petType ->
                        petType.petType == selectedChipText
                    }
                    initRecyclerView(filteredList)
                }else{
                    getData()
                }
            }
        }
    }

    private fun initRecyclerView(petList: List<PetModel>) {
        val _adapter = HomeAdapter(petList)
        val _layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.apply {
            adapter = _adapter
            layoutManager = _layoutManager
        }
    }
}