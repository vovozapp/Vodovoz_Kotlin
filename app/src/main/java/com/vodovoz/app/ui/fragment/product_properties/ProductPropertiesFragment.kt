package com.vodovoz.app.ui.fragment.product_properties

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentProductPropertiesBinding
import com.vodovoz.app.ui.adapter.ProductPropertyGroupsAdapter
import com.vodovoz.app.ui.adapter.ProductPropertyGroupsAdapter.ViewMode
import com.vodovoz.app.ui.model.PropertiesGroupUI

class ProductPropertiesFragment : Fragment() {

    companion object {
        fun newInstance(
            propertiesGroupUIList: List<PropertiesGroupUI>
        ) = ProductPropertiesFragment().apply {
            this.propertiesGroupUIList = propertiesGroupUIList
        }
    }

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private lateinit var binding: FragmentProductPropertiesBinding
    private val productPropertyGroupsAdapter: ProductPropertyGroupsAdapter by lazy { ProductPropertyGroupsAdapter(space) }
    private lateinit var propertiesGroupUIList: List<PropertiesGroupUI>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentProductPropertiesBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initView()
    }.root

    private fun initView() {
        if (productPropertyGroupsAdapter.viewMode == ViewMode.ALL) {
            binding.allProperties.visibility = View.GONE
        }
        binding.allProperties.setOnClickListener {
            productPropertyGroupsAdapter.updateViewMode(ViewMode.ALL)
            binding.allProperties.visibility = View.GONE
        }

        binding.propertiesGroupRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.propertiesGroupRecycler.adapter = productPropertyGroupsAdapter.also {
            it.propertiesGroupUIList = propertiesGroupUIList
        }
    }

}