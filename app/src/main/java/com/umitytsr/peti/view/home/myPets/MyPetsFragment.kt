package com.umitytsr.peti.view.home.myPets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.FragmentMyPetsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPetsFragment : Fragment() {
    private lateinit var binding : FragmentMyPetsBinding
    private val viewModel : MyPetsViewModel by viewModels()
     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         binding = FragmentMyPetsBinding.inflate(inflater,container,false)
         getMyPetList()
         return binding.root
    }

    private fun getMyPetList(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.petListResult.collectLatest {
                initRecyclerView(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.arrowBackButton.setOnClickListener {
            findNavController().navigate(
                MyPetsFragmentDirections.actionMyPetsFragmentToProfileFragment()
            )
        }
    }


    private fun initRecyclerView(petList: List<PetModel>) {
        val _adapter = MyPetsAdapter(petList,viewModel)
        val _layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.myPetsRecyclerView.apply {
            adapter = _adapter
            layoutManager = _layoutManager
        }
    }
}