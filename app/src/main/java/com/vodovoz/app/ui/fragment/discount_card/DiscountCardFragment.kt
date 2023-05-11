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
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentDiscountCardBinding
import com.vodovoz.app.ui.adapter.DiscountCardPropertiesAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscountCardFragment : BaseFragment() {

    private val binding: FragmentDiscountCardBinding by viewBinding {
        FragmentDiscountCardBinding.bind(contentView)
    }
    private val viewModel: DiscountCardViewModel by viewModels()

    private val discountCardPropertiesAdapter = DiscountCardPropertiesAdapter()

    override fun layout(): Int = R.layout.fragment_discount_card

    override fun update() {
        viewModel.fetchData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        update()
    }

    override fun initView() {
        initAppBar()
        initDiscountCardPropertiesRecycler()
        initButtons()
        observeViewModel()
    }

    private fun initAppBar() {
        initToolbar("Активация скидочной карты")
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

        viewModel.activateDiscountCardBundleUILD.observe(viewLifecycleOwner) { activateDiscountCardBundleUI ->
            binding.info.text = activateDiscountCardBundleUI.details.fromHtml()
            initToolbar(activateDiscountCardBundleUI.title)

            discountCardPropertiesAdapter.discountCardPropertyUIList = activateDiscountCardBundleUI.discountCardPropertyUIList
            discountCardPropertiesAdapter.notifyDataSetChanged()
        }

        viewModel.messageLD.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

}