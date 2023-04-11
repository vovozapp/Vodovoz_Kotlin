package com.vodovoz.app.feature.cart.gifts.old

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSelectionGiftsBinding
import com.vodovoz.app.feature.cart.CartFragment
import com.vodovoz.app.feature.cart.gifts.GiftsBottomFragmentArgs
import com.vodovoz.app.feature.cart.gifts.adapter.GiftsFlowAdapter
import com.vodovoz.app.feature.cart.gifts.adapter.GiftsFlowClickListener
import com.vodovoz.app.ui.model.ProductUI

class GiftsBottomFlowFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.bs_selection_gifts

    private val binding: BsSelectionGiftsBinding by viewBinding {
        BsSelectionGiftsBinding.bind(
            contentView
        )
    }

    private val giftsAdapter = GiftsFlowAdapter(getGiftsClickListener())

    private val args: GiftsBottomFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvGifts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGifts.adapter = giftsAdapter
        binding.rvGifts.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.rvGifts.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        top = space
                        bottom = space
                        right = space
                        left = space
                    }
                }
            }
        )

        binding.rvGifts.adapter = giftsAdapter

        giftsAdapter.submitList(args.giftList.toList())

        binding.incHeader.imgClose.setOnClickListener {
            requireDialog().cancel()
        }
        binding.incHeader.tvTitle.text = getString(R.string.gift_selection_title)
    }


    private fun getGiftsClickListener(): GiftsFlowClickListener {
        return object : GiftsFlowClickListener {
            override fun onProductClick(product: ProductUI) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(CartFragment.GIFT_ID, product)
                dialog?.dismiss()
            }
        }
    }
}