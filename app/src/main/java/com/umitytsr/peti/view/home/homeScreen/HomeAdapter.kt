package com.umitytsr.peti.view.home.homeScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umitytsr.peti.R
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.ItemRowPetCardBinding

class HomeAdapter(private var petList: List<PetModel>, private val petItemClickListener: PetItemClickListener) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private val binding: ItemRowPetCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(petModel: PetModel) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(petModel.petImage)
                    .into(petImage)

                petCardView.setOnClickListener {
                    petItemClickListener.petItemClickedListener(petModel)
                }

                petSexTextView.text = petModel.petSex

                petGoalTextView.text = petModel.petGoal

                val context = itemView.context
                if (petModel.petGoal == "Ownership") {
                    petGoalCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red))
                } else {
                    petGoalCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green))
                }
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
    interface PetItemClickListener{
        fun petItemClickedListener(pet : PetModel)
    }
}