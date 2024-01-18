package com.umitytsr.peti.view.home.homeScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umitytsr.peti.R
import com.umitytsr.peti.data.model.PetModel
import com.umitytsr.peti.databinding.ItemRowPetCardBinding
import com.umitytsr.peti.util.Enums

class HomeAdapter(private var petList: List<PetModel>
, private val petItemClickListener: PetItemClickListener) :
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
                val context = itemView.context
                petSexTextView.text = Enums.getPetSexById(petModel.petSex).getString(context)
                petGoalTextView.text = Enums.getPetGoalById(petModel.petGoal).getString(context)

                if (petModel.petGoal.toInt() == 1) {
                    petGoalCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green))
                } else {
                    petGoalCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red))
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