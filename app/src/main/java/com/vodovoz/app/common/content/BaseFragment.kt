package com.vodovoz.app.common.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentBasePageBinding

abstract class BaseFragment : Fragment() {

    private var _viewBinding: FragmentBasePageBinding? = null
    private val viewBinding
        get() = _viewBinding!!

    protected val contentView: View
        get() = requireView().findViewById(R.id.container_base)

    private val loader: View
        get() = viewBinding.loadingContainer

    val refresh: TextView
        get() = viewBinding.update

    protected abstract fun layout(): Int
    protected open fun update() {}
    protected open fun initView() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentBasePageBinding.inflate(inflater, container, false)

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
        initUpdateButton()
    }

    protected fun showLoader() {
        loader.isVisible = true
    }

    protected fun hideLoader() {
        loader.isVisible = false
    }

    protected fun showError(error: ErrorState?) {
        if (error != null) {
            viewBinding.errorContainer.isVisible = true
            viewBinding.errorMessage.text = error.message
        } else {
            viewBinding.errorContainer.isVisible = false
        }
    }

    protected fun hideError() {
        hideLoader()
        contentView.isVisible = true
        viewBinding.errorContainer.isVisible = false
    }

    protected fun initUpdateButton() {
        viewBinding.update.setOnClickListener {
            update()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}