package com.vodovoz.app.feature.profile.userdata

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSelectionGenderBinding
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsController
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders.GenderUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenderSelectionBS : BaseBottomSheetFragment() {

    companion object {
        const val SELECTED_GENDER = "SELECTED_GENDER"
    }

    override fun layout(): Int {
        return R.layout.bs_selection_gender
    }

    private val binding: BsSelectionGenderBinding by viewBinding {
        BsSelectionGenderBinding.bind(contentView)
    }

    private val args: GenderSelectionBSArgs by navArgs()

    private val intervalsController by lazy {
        IntervalsController() {
            val gender = if ((it as GenderUI).name == "Мужской") {
                "MALE"
            } else {
                "FEMALE"
            }
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                SELECTED_GENDER,
                gender
            )
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = if (args.selectedGender == "MALE") {
            listOf(GenderUI("Мужской", true), GenderUI("Женский", false))
        } else {
            listOf(GenderUI("Мужской", false), GenderUI("Женский", true))
        }

        intervalsController.bind(binding.rvGenders, list)
    }
}
