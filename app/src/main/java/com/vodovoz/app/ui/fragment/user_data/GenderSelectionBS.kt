package com.vodovoz.app.ui.fragment.user_data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSelectionGenderBinding
import com.vodovoz.app.ui.adapter.GendersAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class GenderSelectionBS : BottomSheetDialogFragment() {

    companion object {
        public const val SELECTED_GENDER = "SELECTED_GENDER"
    }

    private lateinit var binding: BsSelectionGenderBinding

    private val gendersAdapter = GendersAdapter()
    private val genderList = listOf(Gender.MALE, Gender.FEMALE)
    private lateinit var selectedGender: Gender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        selectedGender = Gender.valueOf(GenderSelectionBSArgs.fromBundle(requireArguments()).selectedGender)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BsSelectionGenderBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        setupGendersRecycler()
    }

    private fun setupGendersRecycler() {
        binding.rvGenders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGenders.adapter = gendersAdapter
        val space8 = resources.getDimension(R.dimen.space_8).toInt()
        binding.rvGenders.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.top = space8
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space8
        }
        gendersAdapter.setupListeners { gender ->
            findNavController().previousBackStackEntry?.savedStateHandle?.set(SELECTED_GENDER, gender.name)
            dismiss()
        }
        gendersAdapter.updateData(
            selectedGender = selectedGender,
            genderList = genderList
        )
    }

}