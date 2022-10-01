package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderGenderBinding
import com.vodovoz.app.ui.fragment.user_data.Gender

class GendersAdapter : RecyclerView.Adapter<GenderVH>() {

    private var genderList = listOf<Gender>()
    private lateinit var selectedGender: Gender
    private lateinit var selectGender: (Gender) -> Unit

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(genderList: List<Gender>, selectedGender: Gender) {
        this.genderList = genderList
        this.selectedGender = selectedGender
        notifyDataSetChanged()
    }

    fun setupListeners(selectGender: (Gender) -> Unit) {
        this.selectGender = selectGender
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = GenderVH(
        binding = ViewHolderGenderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        selectGender = selectGender
    )

    override fun onBindViewHolder(
        holder: GenderVH,
        position: Int
    ) = holder.onBind(genderList[position], genderList[position] == selectedGender)

    override fun getItemCount() = genderList.size

}

class GenderVH(
    val binding: ViewHolderGenderBinding,
    val selectGender: (Gender) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.root.setOnClickListener { selectGender(gender) } }

    private lateinit var gender: Gender

    fun onBind(gender: Gender, isSelected: Boolean) {
        this.gender = gender
        binding.tvName.text = gender.genderName
        when(isSelected) {
            true -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalBlue)
            false -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalGray)
        }

    }

}