package com.vodovoz.app.ui.components.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.databinding.FragmentBaseHiddenBinding

abstract class BaseHiddenFragment<T : ViewModel> : Fragment() {

    private lateinit var rootBinding: FragmentBaseHiddenBinding
    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentBaseHiddenBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        rootBinding = this
        getViewModelClass()?.let { initViewModel(it) }
        setContentView()?.let { rootBinding.contentContainer.addView(it) }
        initView()
    }.root

    protected open fun setContentView(): View? = null
    protected open fun getAppBarView(): View? = null
    protected open fun getViewModelClass(): Class<T>? = null
    protected open fun initView() {}

    protected fun setState(state: State) {
        when (state) {
            State.HIDE -> onStateHide()
            State.SHOW -> onStateShow()
        }
    }

    protected fun onStateHide() {
        rootBinding.root.visibility = View.GONE
    }


    protected fun onStateShow() {
        rootBinding.root.visibility = View.VISIBLE
    }

    @Suppress("UNCHECKED_CAST")
    protected fun viewModel() = viewModel as T

    private fun initViewModel(modelClass: Class<T>){
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[modelClass]
    }

    enum class State {
        HIDE,
        SHOW
    }

}