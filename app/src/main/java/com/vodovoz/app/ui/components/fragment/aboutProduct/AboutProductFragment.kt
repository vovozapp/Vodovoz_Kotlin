package com.vodovoz.app.ui.components.fragment.aboutProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.vodovoz.app.databinding.FragmentAbountProductBinding

class AboutProductFragment : Fragment() {

    companion object {
        private const val ABOUT_TEXT = "ABOUT_TEXT"

        fun newInstance(aboutText: String) = AboutProductFragment().apply {
            arguments = Bundle().also { args ->
                args.putString(ABOUT_TEXT, aboutText)
            }
        }
    }

    private lateinit var binding: FragmentAbountProductBinding
    private var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentAbountProductBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        arguments?.getString(ABOUT_TEXT)?.let { aboutText ->
            binding.aboutProduct.originalText = HtmlCompat.fromHtml(aboutText, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        }
        if (isExpanded) {
            binding.aboutProduct.toggle()
        }
    }.root

    override fun onStop() {
        super.onStop()
        isExpanded = !binding.aboutProduct.collapsed
    }
}