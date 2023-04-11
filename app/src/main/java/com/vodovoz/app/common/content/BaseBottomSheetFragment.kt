package com.vodovoz.app.common.content

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentBottomSheetBaseBinding

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {

    private var _viewBinding: FragmentBottomSheetBaseBinding? = null
    private val viewBinding
        get() = _viewBinding!!

    protected val contentView: View
        get() = requireView().findViewById(R.id.container_base_bottom)

    private val loader: View
        get() = viewBinding.containerProgressBottom

    private val progressBg: View
        get() = viewBinding.progressBgBottom

    val refresh: TextView
        get() = viewBinding.error.refreshTv

    protected abstract fun layout(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.color_transparent)))

        _viewBinding = FragmentBottomSheetBaseBinding.inflate(inflater, container, false)

        viewBinding.containerBaseBottom.runCatching {
            layoutResource = layout()
            val inflate = inflate()
            inflate.id = viewBinding.containerBaseBottom.id
            inflate
        }

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    protected fun showLoader() {
        loader.isVisible = true
        progressBg.isVisible = true
    }

    protected fun hideLoader() {
        loader.isVisible = false
        progressBg.isVisible = false
    }

    protected fun bindErrorRefresh(onRefresh: () -> Unit) {
        refresh.setOnClickListener {
            onRefresh.invoke()
        }
    }

    protected fun showError(error: ErrorState?) {
        if (error != null) {
            with(viewBinding.error) {
                root.isVisible = true
                messageTv.text = error.message
                descTv.text = error.description
                icon.setImageDrawable(androidx.core.content.ContextCompat.getDrawable(requireContext(), error.iconDrawable))
            }
        } else {
            viewBinding.error.root.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    protected fun navigateTo(resId: Int, args: Bundle?, navOptions: NavOptions?) =
        findNavController().navigate(resId, args, navOptions)

    protected fun navigateTo(resId: Int, args: Bundle?) {

    }

    protected fun navigateTo(resId: Int) {

    }

    protected fun navigateUp() = findNavController().navigateUp()
}