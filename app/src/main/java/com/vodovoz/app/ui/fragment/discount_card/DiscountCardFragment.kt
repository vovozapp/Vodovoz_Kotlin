package com.vodovoz.app.ui.fragment.discount_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.databinding.FragmentDiscountCardBinding
import com.vodovoz.app.ui.adapter.DiscountCardPropertiesAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscountCardFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentDiscountCardBinding
    private val viewModel: DiscountCardViewModel by viewModels()

    private val discountCardPropertiesAdapter = DiscountCardPropertiesAdapter()

    override fun update() {
        viewModel.fetchData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        update()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentDiscountCardBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initAppBar()
        initDiscountCardPropertiesRecycler()
        initButtons()
        observeViewModel()
    }

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initDiscountCardPropertiesRecycler() {
        binding.discountCardPropertiesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.discountCardPropertiesRecycler.adapter = discountCardPropertiesAdapter
    }

    private fun initButtons() {
        binding.submit.setOnClickListener {
            viewModel.activateDiscountCard()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.activateDiscountCardBundleUILD.observe(viewLifecycleOwner) { activateDiscountCardBundleUI ->
            binding.info.text = HtmlCompat.fromHtml(activateDiscountCardBundleUI.details, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            binding.toolbar.title = activateDiscountCardBundleUI.title

            discountCardPropertiesAdapter.discountCardPropertyUIList = activateDiscountCardBundleUI.discountCardPropertyUIList
            discountCardPropertiesAdapter.notifyDataSetChanged()
        }

        viewModel.messageLD.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

}