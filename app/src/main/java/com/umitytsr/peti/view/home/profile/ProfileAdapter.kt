package com.umitytsr.peti.view.home.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.ItemRowProfilePetCardBinding

class ProfileAdapter(private val myPetList: List<PetModel>) :
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    inner class ProfileViewHolder(private val binding: ItemRowProfilePetCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(petModel: PetModel) {
            with(binding) {
                petNameTextView.text = petModel.petName
                Glide.with(itemView.context)
                    .load(petModel.petImage)
                    .into(petImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRowProfilePetCardBinding.inflate(layoutInflater, parent, false)

        return ProfileViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return myPetList.size
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(myPetList[position])
    }
}