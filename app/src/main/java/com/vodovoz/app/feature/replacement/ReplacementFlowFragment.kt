package com.vodovoz.app.feature.replacement

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsReplacementProductsBinding
import com.vodovoz.app.ui.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionBSArgs
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionBSDirections
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionViewModel
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view.Divider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReplacementFlowFragment : BottomSheetDialogFragment() {



}