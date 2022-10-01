package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderPopularCategoryBinding
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.view_holder.PopularCategorySliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class PopularCategoriesSliderAdapter(
    private val onPopularClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<PopularCategorySliderViewHolder> (){

    var categoryPopularUIList = listOf<CategoryUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PopularCategorySliderViewHolder(
        binding = ViewHolderSliderPopularCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onPopularClickSubject = onPopularClickSubject
    )

    override fun onBindViewHolder(
        holder: PopularCategorySliderViewHolder,
        position: Int
    ) = holder.onBind(categoryPopularUIList[position])

    override fun getItemCount() = categoryPopularUIList.size

}