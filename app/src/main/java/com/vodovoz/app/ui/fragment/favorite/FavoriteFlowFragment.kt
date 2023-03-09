package com.vodovoz.app.ui.fragment.favorite

import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentMainFavoriteFlowBinding
import com.vodovoz.app.ui.base.content.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_main_favorite_flow

    private val binding: FragmentMainFavoriteFlowBinding by viewBinding { FragmentMainFavoriteFlowBinding.bind(contentView) }

    private val viewModel: FavoriteFlowViewModel by activityViewModels()



}