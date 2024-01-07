package com.umitytsr.peti.view.home.homeScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.ItemRowPetCardBinding

class HomeAdapter(private var petList: List<PetModel>) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private val binding: ItemRowPetCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(petModel: PetModel) {
            with(binding) {
                breedTextView.text = petModel.petBreed
                sexTextView.text = petModel.petSex
                goalTextView.text = petModel.petGoal
                Glide.with(itemView.context)
                    .load(petModel.petImage)
                    .into(petImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRowPetCardBinding.inflate(layoutInflater, parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(petList[position])
    }

    override fun getItemCount(): Int {
        return petList.size
    }
}