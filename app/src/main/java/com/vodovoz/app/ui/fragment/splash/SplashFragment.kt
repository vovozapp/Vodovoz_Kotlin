package com.vodovoz.app.ui.fragment.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.vodovoz.app.R
import com.vodovoz.app.ui.base.BaseFragment
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.fragment.home.HomeViewModel
import com.vodovoz.app.ui.fragment.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_splash

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> {}
                is ViewState.Loading -> {}
                is ViewState.Error -> {}
                is ViewState.Success -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fcvMainContainer, MainFragment())
                        .commit()
                }
            }
        }
    }
}