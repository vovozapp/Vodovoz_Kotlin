package com.vodovoz.app.ui.components.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.vodovoz.app.databinding.FragmentBaseBinding

abstract class ViewStateBaseDialogFragment : DialogFragment() {

    private lateinit var rootBinding: FragmentBaseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentBaseBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        rootBinding = this
        setContentView(inflater, this.root)?.let { rootBinding.contentContainer.addView(it) }
        initView()
        initUpdateButton()
    }.root

    protected open fun setContentView(inflater: LayoutInflater, container: ViewGroup): View? = null
    protected open fun initView() {}
    protected abstract fun update()

    protected fun onStateLoading() {
        rootBinding.errorContainer.visibility = View.INVISIBLE
        rootBinding.contentContainer.visibility = View.INVISIBLE
        rootBinding.loadingContainer.visibility = View.VISIBLE
    }

    protected fun onStateSuccess() {
        rootBinding.errorContainer.visibility = View.INVISIBLE
        rootBinding.loadingContainer.visibility = View.INVISIBLE
        rootBinding.contentContainer.visibility = View.VISIBLE
    }

    protected fun onStateError(errorMessage: String?) {
        errorMessage?.let { rootBinding.errorMessage.text = errorMessage }
        rootBinding.contentContainer.visibility = View.INVISIBLE
        rootBinding.loadingContainer.visibility = View.INVISIBLE
        rootBinding.errorContainer.visibility = View.VISIBLE
    }

    protected fun onStateHide() {
        rootBinding.contentContainer.visibility = View.GONE
        rootBinding.loadingContainer.visibility = View.GONE
        rootBinding.errorContainer.visibility = View.GONE
    }

    protected fun initUpdateButton() {
        rootBinding.update.setOnClickListener { update() }
    }

}