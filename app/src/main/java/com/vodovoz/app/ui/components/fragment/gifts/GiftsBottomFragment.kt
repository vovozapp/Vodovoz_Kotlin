package com.vodovoz.app.ui.components.fragment.gifts

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BottomFragmentGiftsBinding
import com.vodovoz.app.ui.components.adapter.GiftsAdapter
import com.vodovoz.app.ui.components.fragment.cart.CartFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class GiftsBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomFragmentGiftsBinding

    private var disposable: Disposable? = null
    private val onPickUpGiftSubject: PublishSubject<ProductUI> = PublishSubject.create()
    private val giftsAdapter = GiftsAdapter(onPickUpGiftSubject = onPickUpGiftSubject)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomFragmentGiftsBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initDialog()
        initView()
    }.root

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.giftRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.giftRecycler.adapter = giftsAdapter
        binding.giftRecycler.setScrollElevation(binding.appBar)
        binding.giftRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.giftRecycler.addItemDecoration(
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

        giftsAdapter.productUIList  = GiftsBottomFragmentArgs.fromBundle(requireArguments()).giftList.toList()
        binding.giftRecycler.adapter = giftsAdapter

        binding.close.setOnClickListener {
            requireDialog().cancel()
        }
    }

    override fun onStart() {
        super.onStart()
        disposable = onPickUpGiftSubject.subscribeBy { gift ->
            findNavController().previousBackStackEntry?.savedStateHandle?.set(CartFragment.GIFT_ID, gift)
            dialog?.dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }
}