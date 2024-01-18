package com.umitytsr.peti.view.home.homeScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.FragmentHomeBinding
import com.umitytsr.peti.view.home.homeScreen.bottomSheet.FilterBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeAdapter.PetItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel : HomeViewModel by activityViewModels()
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
            viewModel.petListResult.collect {
                initUi(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.filterFAB.setOnClickListener {
            val filterBottomSheet = FilterBottomSheetFragment()
            filterBottomSheet.show(parentFragmentManager, FilterBottomSheetFragment.TAG)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.petListResult.collect {
                if (it.isNotEmpty()){
                    initUi(it)
                }else{
                    getData()
                }
            }
        }
    }

    private fun initUi(petList: List<PetModel>){
        if (petList.isNotEmpty()){
            with(binding){
                emtyListImageView.visibility = View.GONE
                emptyListTextView.visibility = View.GONE
                youHaventTextView.visibility = View.GONE
                homeFragmentRecyclerView.visibility = View.VISIBLE
            }
            initRecyclerView(petList)
        }else{
            with(binding){
                emtyListImageView.visibility = View.VISIBLE
                emptyListTextView.visibility= View.VISIBLE
                youHaventTextView.visibility = View.VISIBLE
                homeFragmentRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun initRecyclerView(petList: List<PetModel>) {
        val _adapter = HomeAdapter(petList,this@HomeFragment)
        val _layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.homeFragmentRecyclerView.apply {
            adapter = _adapter
            layoutManager = _layoutManager
        }
    }

    override fun petItemClickedListener(pet: PetModel) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToInfoFragment(pet,"homeFragment")
        )
    }
}