package com.umitytsr.peti.view.home.myPets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.ItemRowMyPetCardBinding

class MyPetsAdapter(private val myPetList: List<PetModel>,private val viewModel: MyPetsViewModel) :
    RecyclerView.Adapter<MyPetsAdapter.MyPetsViewHolder>() {

    inner class MyPetsViewHolder(private val binding: ItemRowMyPetCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(petModel: PetModel) {
            with(binding) {
                petNameTextView.text = petModel.petName
                Glide.with(itemView.context)
                    .load(petModel.petImage)
                    .into(petImage)

                deleteButton.setOnClickListener {
                    viewModel.deletePet(petModel.petImage)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPetsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRowMyPetCardBinding.inflate(layoutInflater,parent,false)

        return MyPetsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return myPetList.size
    }

    override fun onBindViewHolder(holder: MyPetsViewHolder, position: Int) {
        holder.bind(myPetList[position])
    }
}