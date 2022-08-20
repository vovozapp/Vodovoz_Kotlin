package com.vodovoz.app.ui.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vodovoz.app.databinding.FragmentBaseBinding
import com.vodovoz.app.util.LogSettings

abstract class ViewStateBaseFragment : Fragment() {

    private lateinit var rootBinding: FragmentBaseBinding
    private var isLoading = false

    final override fun onCreateView(
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
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initUpdateButton()
    }

    protected open fun setContentView(inflater: LayoutInflater, container: ViewGroup): View? = null
    protected open fun initView() {}
    protected abstract fun update()

    protected fun onStateLoading() {
        isLoading = true
        rootBinding.errorContainer.visibility = View.INVISIBLE
        rootBinding.contentContainer.visibility = View.INVISIBLE
        rootBinding.loadingContainer.visibility = View.VISIBLE
    }

    protected fun onStateSuccess() {
        isLoading = false
        rootBinding.errorContainer.visibility = View.INVISIBLE
        rootBinding.loadingContainer.visibility = View.INVISIBLE
        rootBinding.contentContainer.visibility = View.VISIBLE
    }

    protected fun onStateError(errorMessage: String?) {
        isLoading = false
        errorMessage?.let { rootBinding.errorMessage.text = errorMessage }
        rootBinding.contentContainer.visibility = View.INVISIBLE
        rootBinding.loadingContainer.visibility = View.INVISIBLE
        rootBinding.errorContainer.visibility = View.VISIBLE
    }

    protected fun onStateHide() {
        isLoading = false
        rootBinding.contentContainer.visibility = View.GONE
        rootBinding.loadingContainer.visibility = View.GONE
        rootBinding.errorContainer.visibility = View.GONE
    }

    protected fun initUpdateButton() {
        rootBinding.update.setOnClickListener {
            if (!isLoading) update()
        }
    }


}