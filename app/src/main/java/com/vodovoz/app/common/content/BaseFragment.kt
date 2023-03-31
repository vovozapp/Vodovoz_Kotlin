package com.vodovoz.app.common.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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


    val refresh: ImageView
        get() = viewBinding.error.refreshIv

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

    protected fun showError(error: ErrorState?) {
        if (error != null) {
            with(viewBinding.error) {
                root.isVisible = true
                messageTv.text = error.message
                icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), error.iconDrawable))
            }
        } else {
            viewBinding.error.root.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    protected fun initSearchToolbar(onContainerClick: () -> Unit, bindEt: () -> Unit) {
        viewBinding.appbarLayout.isVisible = true
        viewBinding.searchAppBar.isVisible = true

        viewBinding.searchContainer.clSearchContainer.setOnClickListener {
            onContainerClick.invoke()
        }

        viewBinding.searchContainer.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                bindEt.invoke()
            }
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

    protected fun bindToolbar(showNavIcon: Boolean, title: String, showLogo: Boolean = false, transparentBg: Boolean = false) {
        viewBinding.appbarLayout.isVisible = true

        /*toolbar.navigationIcon = if (showNavIcon) {
            view?.context?.drawable(R.drawable.ic_arrow_back)
        } else {
            null
        }

        toolbar.logo = if (showLogo) {
            view?.context?.drawable(R.drawable.ic_logo_new)
        } else {
            null
        }

        toolbar.setBackgroundColor(if (transparentBg) {
            requireContext().getColor(R.color.color_transparent)
        } else {
            requireContext().getColor(R.color.carrot8)
        })

        if (title.isNotBlank()) toolbar.title = title
        toolbar.setNavigationOnClickListener { navigateUp() }*/
    }

    protected fun navigateTo(resId: Int, args: Bundle? = null, navOptions: NavOptions? = null) =
        findNavController().navigate(resId, args, navOptions)

    protected fun navigateUp() = findNavController().navigateUp()
}