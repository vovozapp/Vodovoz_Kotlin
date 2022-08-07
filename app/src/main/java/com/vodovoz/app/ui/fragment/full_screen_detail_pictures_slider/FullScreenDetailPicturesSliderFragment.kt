package com.vodovoz.app.ui.fragment.full_screen_detail_pictures_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.databinding.DialogProductImagesPagerBinding
import com.vodovoz.app.ui.adapter.FullScreenDetailPicturesAdapter

class FullScreenDetailPicturesSliderFragment : DialogFragment() {

    private var startPosition = 0
    private lateinit var detailPictureUrlList: List<String>
    private lateinit var binding: DialogProductImagesPagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        getArgs()
    }

    private fun getArgs() {
        FullScreenDetailPicturesSliderFragmentArgs.fromBundle(requireArguments()).let { args ->
            this.startPosition = args.startPosition
            this.detailPictureUrlList = args.detailPictureList.toList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogProductImagesPagerBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initView()
    }.root

    private fun initView() {
        binding.vpImages.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpImages.adapter = FullScreenDetailPicturesAdapter(detailPictureUrlList)
        binding.vpImages.currentItem = startPosition

        binding.imgClose.setOnClickListener { findNavController().popBackStack() }
    }

}