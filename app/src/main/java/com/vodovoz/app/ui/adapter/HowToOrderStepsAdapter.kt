package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.PagerHowToOrderBinding
import com.vodovoz.app.ui.view_holder.HowToOrderStepPage
import com.vodovoz.app.ui.model.HowToOrderStepUI

class HowToOrderStepsAdapter(
    val howToOrderStepUIList: List<HowToOrderStepUI>
) : RecyclerView.Adapter<HowToOrderStepPage>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = HowToOrderStepPage(
        binding = PagerHowToOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: HowToOrderStepPage,
        position: Int
    ) = holder.onBind(howToOrderStepUIList[position])

    override fun getItemCount() = howToOrderStepUIList.size

}