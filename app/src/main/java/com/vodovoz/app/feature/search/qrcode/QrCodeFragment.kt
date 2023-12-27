package com.vodovoz.app.feature.search.qrcode

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.zxing.Result
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentQrCodeBinding
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView

@AndroidEntryPoint
class QrCodeFragment : Fragment(R.layout.fragment_qr_code), ZXingScannerView.ResultHandler {

    private val binding: FragmentQrCodeBinding by viewBinding(FragmentQrCodeBinding::bind)

    private val viewModel: QrCodeViewModel by viewModels()

    private var scannerView: ZXingScannerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannerView = ZXingScannerView(requireContext())
        binding.frame.addView(scannerView)

        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect {
                        when (it) {
                            is QrCodeViewModel.QrCodeEvents.Success -> {
                                findNavController().navigate(
                                    QrCodeFragmentDirections.actionToProductDetailFragment(
                                        it.id.toLong()
                                    )
                                )
                            }
                            is QrCodeViewModel.QrCodeEvents.Error -> {
                                requireActivity().snack(it.message)
                            }
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView?.stopCamera()
    }

    override fun handleResult(result: Result?) {
        viewModel.startSearchByQrCode(result?.text)
    }
}