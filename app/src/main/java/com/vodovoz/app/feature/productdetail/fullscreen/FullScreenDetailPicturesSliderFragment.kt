package com.vodovoz.app.feature.productdetail.fullscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.databinding.DialogProductImagesPagerBinding
import com.vodovoz.app.feature.productdetail.fullscreen.adapter.FullScreenDetailPicturesAdapter

class FullScreenDetailPicturesSliderFragment : DialogFragment(R.layout.dialog_product_images_pager) {

    private val binding: DialogProductImagesPagerBinding by viewBinding(DialogProductImagesPagerBinding::bind)

    private val args: FullScreenDetailPicturesSliderFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.vpImages.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpImages.adapter = FullScreenDetailPicturesAdapter(args.detailPictureList.toList())
        binding.vpImages.currentItem = args.startPosition
        binding.dotsIndicator.attachTo(binding.vpImages)

        binding.imgClose.setOnClickListener { findNavController().popBackStack() }
    }

}