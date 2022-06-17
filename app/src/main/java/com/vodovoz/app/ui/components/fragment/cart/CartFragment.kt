package com.vodovoz.app.ui.components.fragment.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vodovoz.app.databinding.FragmentMainCartBinding

class CartFragment : Fragment() {

    private lateinit var binding: FragmentMainCartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainCartBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

}