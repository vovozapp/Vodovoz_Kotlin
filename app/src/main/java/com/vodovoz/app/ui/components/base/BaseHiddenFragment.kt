package com.vodovoz.app.ui.components.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.databinding.FragmentBaseBinding
import com.vodovoz.app.databinding.FragmentBaseHiddenBinding

abstract class BaseHiddenFragment : Fragment() {

    private lateinit var rootBinding: FragmentBaseHiddenBinding

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
        setContentView(inflater, this.root)?.let { rootBinding.contentContainer.addView(it) }
        initView()
    }.root

    protected open fun setContentView(inflater: LayoutInflater, container: ViewGroup): View? = null
    protected open fun initView() {}

    fun hide() {
        rootBinding.root.visibility = View.GONE
    }

    fun show() {
        rootBinding.root.visibility = View.VISIBLE
    }

}