package com.vodovoz.app.ui.components.fragment.productProperties

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentProductPropertiesBinding
import com.vodovoz.app.ui.components.adapter.productPropertiesGroupsAdapter.PropertiesGroupsAdapter
import com.vodovoz.app.ui.components.adapter.productPropertiesGroupsAdapter.ViewMode
import com.vodovoz.app.ui.model.PropertiesGroupUI
import com.vodovoz.app.util.LogSettings

class ProductPropertiesFragment : Fragment() {

    companion object {
        fun newInstance(
            propertiesGroupUIList: List<PropertiesGroupUI>
        ) = ProductPropertiesFragment().apply {
            this.propertiesGroupUIList = propertiesGroupUIList
        }
    }

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }

    private lateinit var binding: FragmentProductPropertiesBinding
    private val propertiesGroupsAdapter: PropertiesGroupsAdapter by lazy { PropertiesGroupsAdapter(space) }
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
        initPropertiesGroupRecycler()
        initAllPropertiesButton()
    }.root

    private fun initPropertiesGroupRecycler() {
        binding.propertiesGroupRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.propertiesGroupRecycler.adapter = propertiesGroupsAdapter.also {
            it.propertiesGroupUIList = propertiesGroupUIList
        }
    }

    private fun initAllPropertiesButton() {
        if (propertiesGroupsAdapter.viewMode == ViewMode.ALL) {
            binding.allProperties.visibility = View.GONE
        }
        binding.allProperties.setOnClickListener {
            propertiesGroupsAdapter.updateViewMode(ViewMode.ALL)
            binding.allProperties.visibility = View.GONE
        }
    }

}