package com.vodovoz.app.ui.components.adapter.popularSliderAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderPopularCategoryBinding
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PopularSliderAdapter(
    private val onPopularClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<PopularSliderViewHolder> (){

    var categoryPopularUIList = listOf<CategoryUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PopularSliderViewHolder(
        binding = ViewHolderSliderPopularCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onPopularClickSubject = onPopularClickSubject
    )

    override fun onBindViewHolder(
        holder: PopularSliderViewHolder,
        position: Int
    ) = holder.onBind(categoryPopularUIList[position])

    override fun getItemCount() = categoryPopularUIList.size

}