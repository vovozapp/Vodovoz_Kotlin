package com.vodovoz.app.common.content

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentBaseFlowBinding
import com.vodovoz.app.util.extensions.drawable

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
            //showLoaderWithBg(false)
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

    protected fun initSearchToolbar(onContainerClick: () -> Unit, bindEt: () -> Unit, onQrCodeClick: () -> Unit = {}, onMicClick: () -> Unit = {}, showBackBtn: Boolean = false) {
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

        viewBinding.searchContainer.imgQr.setOnClickListener {
            onQrCodeClick.invoke()
        }

        viewBinding.searchContainer.imgMicro.setOnClickListener {
            onMicClick.invoke()
        }
    }

    protected fun initFilterToolbar(showBackBtn: Boolean = true, onFilterBtnClick: () -> Unit) {
        viewBinding.appbarLayout.isVisible = true
        viewBinding.appBarFilter.root.isVisible = true
        viewBinding.appBarFilter.imgBack.setOnClickListener { findNavController().popBackStack() }
        viewBinding.appBarFilter.imgBack.isVisible = showBackBtn

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

    protected fun setToolbarDropDownTitle(title: String, showArrowDropDown: Boolean = true) {
        viewBinding.appBarDropDown.tvDropDownTitle.text = title

        if (showArrowDropDown) {
            viewBinding.appBarDropDown.tvDropDownTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, requireContext().drawable(R.drawable.ic_drop_down),null)
        } else {
            viewBinding.appBarDropDown.tvDropDownTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null,null)
        }
    }

    protected fun initToolbar(titleText: String, showSearch: Boolean = false, showNavBtn: Boolean = true, addAction: Boolean = false, provider: MenuProvider? = null, doAfterTextChanged: (query: String) -> Unit = {}) {
        if (addAction) {
            viewBinding.appBarDef.tbToolbar.overflowIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_more_actions)
        }
        if (provider != null) {
            viewBinding.appBarDef.tbToolbar.addMenuProvider(provider)
        }
        viewBinding.appbarLayout.isVisible = true
        viewBinding.appBarDef.imgSearch.isVisible = showSearch
        if (showNavBtn) {
            viewBinding.appBarDef.imgBack.isVisible = true
            viewBinding.appBarDef.tvTitle.setPadding(0,0,0,0)
        } else {
            viewBinding.appBarDef.imgBack.isVisible = false
            viewBinding.appBarDef.tvTitle.setPadding(50,0,0,0)
        }
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

    fun showHideDefToolbarItem(itemId: Int, show: Boolean) {
        val menuItem = viewBinding.appBarDef.tbToolbar.menu.findItem(itemId)
        menuItem.isVisible = show
    }

    fun setMicroEnabled(enabled: Boolean) {
        if (enabled) {
            viewBinding.searchContainer.imgMicro.background = requireContext().drawable(R.drawable.green_oval)
            ImageViewCompat.setImageTintList(viewBinding.searchContainer.imgMicro, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)))
        } else {
            viewBinding.searchContainer.imgMicro.background = null
            ImageViewCompat.setImageTintList(viewBinding.searchContainer.imgMicro, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.micro)))
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