package com.vodovoz.app.ui.fragment.home_triple_navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vodovoz.app.databinding.FragmentTripleNavigationHomeBinding
import com.vodovoz.app.ui.interfaces.IOnLastPurschasesClick
import com.vodovoz.app.ui.interfaces.IOnOrdersHistoryClick
import com.vodovoz.app.ui.interfaces.IOnShowAllFavoriteClick

class TripleNavigationHomeFragment : Fragment() {

    private lateinit var iOnLastPurchasesClick: IOnLastPurschasesClick
    private lateinit var iOnOrdersHistoryClick: IOnOrdersHistoryClick
    private lateinit var iOnShowAllFavoriteClick: IOnShowAllFavoriteClick

    private lateinit var binding: FragmentTripleNavigationHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTripleNavigationHomeBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initView()
    }.root

    private fun initView() {
        binding.lastPurchases.setOnClickListener { iOnLastPurchasesClick.onLastPurschasesClick() }
        binding.favorite.setOnClickListener { iOnShowAllFavoriteClick.onShowAllFavoriteClick() }
        binding.ordersHistory.setOnClickListener { iOnOrdersHistoryClick.onOrdersHistoryCLick() }
    }

    fun initCallbacks(
        iOnLastPurchasesClick: IOnLastPurschasesClick,
        iOnOrdersHistoryClick: IOnOrdersHistoryClick,
        iOnShowAllFavoriteClick: IOnShowAllFavoriteClick
    ) {
        this.iOnLastPurchasesClick = iOnLastPurchasesClick
        this.iOnShowAllFavoriteClick = iOnShowAllFavoriteClick
        this.iOnOrdersHistoryClick = iOnOrdersHistoryClick
    }

}