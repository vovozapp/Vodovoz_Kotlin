package com.vodovoz.app.common.content

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
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

    protected val composeView: ComposeView
        get() = requireView().findViewById(R.id.composeView)

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
        savedInstanceState: Bundle?,
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
                icon.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        error.iconDrawable
                    )
                )
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

    protected fun initSearchToolbar(
        title: String? = null,
        onContainerClick: () -> Unit,
        bindEt: () -> Unit,
        onQrCodeClick: () -> Unit = {},
        onMicClick: () -> Unit = {},
        showBackBtn: Boolean = false,
    ) {
        viewBinding.searchContainer.etSearch.setHint(title)
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

    protected fun initFilterToolbar(showBackBtn: Boolean = true, filterCount: Int = 0, onFilterBtnClick: () -> Unit) {
        viewBinding.appbarLayout.isVisible = true
        viewBinding.appBarFilter.root.isVisible = true
        viewBinding.appBarFilter.imgBack.setOnClickListener { findNavController().popBackStack() }
        viewBinding.appBarFilter.imgBack.isVisible = showBackBtn

        viewBinding.appBarFilter.clFilter.setOnClickListener {
            onFilterBtnClick.invoke()
        }
        if(filterCount > 0) {
            viewBinding.appBarFilter.tvFiltersAmount.text = filterCount.toString()
            viewBinding.appBarFilter.tvFiltersAmount.visibility = View.VISIBLE
        } else {
            viewBinding.appBarFilter.tvFiltersAmount.visibility = View.INVISIBLE
        }
    }

    protected fun initToolbarDropDown(titleText: String, showBackBtn: Boolean = true, onTitleClick: () -> Unit) {
        viewBinding.appbarLayout.isVisible = true
        viewBinding.appBarDropDown.root.isVisible = true

        viewBinding.appBarDropDown.imgBack.isVisible = showBackBtn
        viewBinding.appBarDropDown.imgBack.setOnClickListener { findNavController().popBackStack() }

        viewBinding.appBarDropDown.tvDropDownTitle.setOnClickListener {
            onTitleClick.invoke()
        }

        viewBinding.appBarDropDown.tvDropDownTitle.text = titleText
    }

    protected fun setToolbarDropDownTitle(title: String, showArrowDropDown: Boolean = true) {
        viewBinding.appBarDropDown.tvDropDownTitle.text = title

        if (showArrowDropDown) {
            viewBinding.appBarDropDown.tvDropDownTitle.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                requireContext().drawable(R.drawable.ic_drop_down),
                null
            )
        } else {
            viewBinding.appBarDropDown.tvDropDownTitle.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
        }
    }

    protected fun initToolbarMyData(
        onLogout: () -> Unit,
        onNavigateBack: () -> Unit = { findNavController().navigateUp() }
    ){
        viewBinding.appbarLayout.isVisible = true
        val appBarUserDataBinding = viewBinding.appBarUserData
        appBarUserDataBinding.root.visibility = View.VISIBLE
        appBarUserDataBinding.root.isVisible = true

        appBarUserDataBinding.imgBack.setOnClickListener {
            onNavigateBack()
        }

        appBarUserDataBinding.imgLogout.setOnClickListener {
            onLogout()
        }
    }

    protected fun initToolbar(
        titleText: String,
        showSearch: Boolean = false,
        showNavBtn: Boolean = true,
        addAction: Boolean = false,
        provider: MenuProvider? = null,
        doAfterTextChanged: (query: String) -> Unit = {},
    ) {
        with(viewBinding.appBarDef) {
            if (addAction) {
                tbToolbar.overflowIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_more_actions)
            }
            if (provider != null) {
                tbToolbar.addMenuProvider(provider)
            }
            viewBinding.appbarLayout.isVisible = true
            imgSearch.visibility = if(showSearch) View.VISIBLE else View.INVISIBLE
            if (showNavBtn) {
                imgBack.isVisible = true
                tvTitle.setPadding(0, 0, 0, 0)
            } else {
                imgBack.isVisible = false
                tvTitle.setPadding(50, 0, 0, 0)
            }
            root.isVisible = true
            tvTitle.text = titleText

            imgBack.setOnClickListener { findNavController().popBackStack() }

            imgSearch.setOnClickListener {
                llTitleContainer.visibility = View.GONE
                llSearchContainer.visibility = View.VISIBLE
            }
            imgClear.setOnClickListener {
                etSearch.setText("")
                llTitleContainer.visibility = View.VISIBLE
                llSearchContainer.visibility = View.GONE
            }
            etSearch.doAfterTextChanged { query ->
                when (query.toString().isEmpty()) {
                    true -> imgClear.visibility = View.GONE
                    false -> imgClear.visibility = View.VISIBLE
                }

                doAfterTextChanged.invoke(query.toString())
            }
        }
    }

    fun hideToolbar(){
        viewBinding.appBarDef.root.visibility = View.GONE
    }

    fun showHideDefToolbarItem(itemId: Int, show: Boolean) {
        val menuItem = viewBinding.appBarDef.tbToolbar.menu.findItem(itemId)
        menuItem.isVisible = show
    }

    fun setMicroEnabled(enabled: Boolean) {
        if (enabled) {
            viewBinding.searchContainer.imgMicro.background =
                requireContext().drawable(R.drawable.green_oval)
            ImageViewCompat.setImageTintList(
                viewBinding.searchContainer.imgMicro,
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            )
        } else {
            viewBinding.searchContainer.imgMicro.background = null
            ImageViewCompat.setImageTintList(
                viewBinding.searchContainer.imgMicro,
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.micro))
            )
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

    protected fun hideSoftKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}