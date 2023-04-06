package com.vodovoz.app.common.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentBaseFlowBinding
import com.vodovoz.app.databinding.FragmentBasePageBinding
import com.vodovoz.app.databinding.ViewSearchBinding

abstract class BaseFragment : Fragment() {

    private var _viewBinding: FragmentBaseFlowBinding? = null
    private val viewBinding
        get() = _viewBinding!!

    protected val contentView: View
        get() = requireView().findViewById(R.id.container_base)

    private val loader: View
        get() = viewBinding.containerProgress

    private val progressBg: View
        get() = viewBinding.progressBg


    val refresh: TextView
        get() = viewBinding.error.refreshTv

    protected abstract fun layout(): Int
    protected open fun update() {}
    protected open fun initView() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentBaseFlowBinding.inflate(inflater, container, false)

        viewBinding.containerBase.runCatching {
            layoutResource = layout()
            val inflate = inflate()
            inflate.id = viewBinding.containerBase.id
            inflate
        }

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    protected fun showLoader() {
        loader.isVisible = true
    }

    protected fun hideLoader() {
        loader.isVisible = false
    }

    protected fun showLoaderWithBg(boolean: Boolean) {
        loader.isVisible = boolean
        progressBg.isVisible = boolean
    }

    protected fun showError(error: ErrorState?) {
        if (error != null) {
            with(viewBinding.error) {
                root.isVisible = true
                messageTv.text = error.message
                descTv.text = error.description
                icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), error.iconDrawable))
            }
            viewBinding.appbarLayout.isEnabled = false
            showLoaderWithBg(false)
        } else {
            viewBinding.error.root.isVisible = false

            viewBinding.appbarLayout.isEnabled = true
        }
    }

    protected fun bindErrorRefresh(onRefresh: () -> Unit) {
        refresh.setOnClickListener {
            onRefresh.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    protected fun initSearchToolbar(onContainerClick: () -> Unit, bindEt: () -> Unit, showBackBtn: Boolean = false) {
        viewBinding.appbarLayout.isVisible = true
        viewBinding.searchAppBar.isVisible = true
        viewBinding.imgBack.setOnClickListener { findNavController().popBackStack() }
        viewBinding.imgBack.isVisible = showBackBtn

        viewBinding.searchContainer.clSearchContainer.setOnClickListener {
            onContainerClick.invoke()
        }

        viewBinding.searchContainer.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                bindEt.invoke()
            }
        }
    }

    protected fun initFilterToolbar(showBackBtn: Boolean = true, onFilterBtnClick: () -> Unit) {
        viewBinding.appbarLayout.isVisible = true
        viewBinding.appBarFilter.root.isVisible = true
        viewBinding.imgBack.setOnClickListener { findNavController().popBackStack() }
        viewBinding.imgBack.isVisible = showBackBtn

        viewBinding.appBarFilter.filter.setOnClickListener {
            onFilterBtnClick.invoke()
        }
    }

    protected fun initToolbarDropDown(titleText: String, onTitleClick: () -> Unit)  {
        viewBinding.appbarLayout.isVisible = true
        viewBinding.appBarDropDown.root.isVisible = true

        viewBinding.appBarDropDown.imgBack.setOnClickListener { findNavController().popBackStack() }

        viewBinding.appBarDropDown.tvDropDownTitle.setOnClickListener {
            onTitleClick.invoke()
        }

        viewBinding.appBarDropDown.tvDropDownTitle.text = titleText
    }

    protected fun initToolbar(titleText: String, showSearch: Boolean = false, showNavBtn: Boolean = true, addAction: Boolean = false, doAfterTextChanged: (query: String) -> Unit = {}) {
        if (addAction) {
            (requireActivity() as AppCompatActivity).setSupportActionBar(viewBinding.appBarDef.tbToolbar)
            viewBinding.appBarDef.tbToolbar.overflowIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_more_actions)
        }
        viewBinding.appbarLayout.isVisible = true
        viewBinding.appBarDef.imgSearch.isVisible = showSearch
        viewBinding.appBarDef.imgBack.isVisible = showNavBtn
        viewBinding.appBarDef.root.isVisible = true
        viewBinding.appBarDef.tvTitle.text = titleText

        viewBinding.appBarDef.imgBack.setOnClickListener { findNavController().popBackStack() }

        viewBinding.appBarDef.imgSearch.setOnClickListener {
            viewBinding.appBarDef.llTitleContainer.visibility = View.GONE
            viewBinding.appBarDef.llSearchContainer.visibility = View.VISIBLE
        }
        viewBinding.appBarDef.imgClear.setOnClickListener {
            viewBinding.appBarDef.etSearch.setText("")
            viewBinding.appBarDef.llTitleContainer.visibility = View.VISIBLE
            viewBinding.appBarDef.llSearchContainer.visibility = View.GONE
        }
        viewBinding.appBarDef.etSearch.doAfterTextChanged { query ->
            when(query.toString().isEmpty()) {
                true -> viewBinding.appBarDef.imgClear.visibility = View.GONE
                false -> viewBinding.appBarDef.imgClear.visibility = View.VISIBLE
            }

            doAfterTextChanged.invoke(query.toString())
        }
    }

    override fun onResume() {
        super.onResume()

        if (viewBinding.appBarDef.etSearch.text.isNullOrBlank().not()) {
            viewBinding.appBarDef.llTitleContainer.visibility = View.GONE
            viewBinding.appBarDef.llSearchContainer.visibility = View.VISIBLE
        }
    }

    protected fun navigateTo(resId: Int, args: Bundle? = null, navOptions: NavOptions? = null) =
        findNavController().navigate(resId, args, navOptions)

    protected fun navigateUp() = findNavController().navigateUp()
}