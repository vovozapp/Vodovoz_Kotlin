package com.vodovoz.app.ui.components.fragment.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vodovoz.app.databinding.FragmentMainFavoriteBinding

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentMainFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainFavoriteBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

}